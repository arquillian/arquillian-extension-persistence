package org.arquillian.ape.rest.postman.runner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.arquillian.ape.rest.postman.runner.model.Body;
import org.arquillian.ape.rest.postman.runner.model.Collection;
import org.arquillian.ape.rest.postman.runner.model.CompleteUrl;
import org.arquillian.ape.rest.postman.runner.model.Domain;
import org.arquillian.ape.rest.postman.runner.model.Folder;
import org.arquillian.ape.rest.postman.runner.model.Item;
import org.arquillian.ape.rest.postman.runner.model.ItemItem;
import org.arquillian.ape.rest.postman.runner.model.Method;
import org.arquillian.ape.rest.postman.runner.model.Path;
import org.arquillian.ape.rest.postman.runner.model.Request;
import org.arquillian.ape.rest.postman.runner.model.Url;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

class CollectionLoader {

    // ThreadLocal is the only way to share data between deserializers in a thread-safe way
    private ThreadLocal<VariableSubstitutor> variableSubstitutorThreadLocal = new ThreadLocal<>();

    public Collection load(String location, Map<String, String> externalVariables) throws IOException {

        try (InputStream collection = CollectionLoader.class.getResourceAsStream(location)) {
            final Map<String, String> variables = new HashMap<>();

            final JsonParser jsonParser = new JsonParser();
            final JsonObject collectionJsonDocument = jsonParser.parse(new InputStreamReader(collection)).getAsJsonObject();
            variables.putAll(loadVariables(collectionJsonDocument));
            variables.putAll(externalVariables);
            variables.putAll(System.getenv());

            final VariableSubstitutor variableSubstitutor = new VariableSubstitutor(variables);
            variableSubstitutorThreadLocal.set(variableSubstitutor);

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Item.class, new ItemDeserializer());
            gsonBuilder.registerTypeAdapter(Url.class, new UrlDeserializer());

            return gsonBuilder.create().fromJson(collectionJsonDocument, Collection.class);
        }

    }

    private Map<String, String> loadVariables(JsonObject collectionJsonDocument) {

        final Map<String, String> variables = new HashMap<>();

        if (collectionJsonDocument.has("variables")) {
            final JsonArray jsonVariables = collectionJsonDocument.getAsJsonArray("variables");

            for (JsonElement jsonVariable : jsonVariables) {
                final JsonObject variable = jsonVariable.getAsJsonObject();
                variables.put(variable.get("id").getAsString(), variable.get("value").getAsString());
            }
        }

        return variables;
    }

    private String applyVariables(String element) {
        return VariablesParser.parseExpressions(element, variableSubstitutorThreadLocal.get().getVariables());
    }

    private static class VariableSubstitutor implements JsonDeserializer<String> {

        private Map<String, String> variables = new HashMap<>();

        public VariableSubstitutor(Map<String, String> variables) {
            this.variables = variables;
        }

        @Override
        public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            if (json.isJsonPrimitive()) {
                return VariablesParser.parseExpressions(json.getAsString(), variables);
            }

            return null;
        }

        public Map<String, String> getVariables() {
            return variables;
        }
    }

    private static class VariablesParser {

        private static final String START_EXPRESSION = "{{";
        private static final String END_EXPRESSION = "}}";

        private static String parseExpressions(final String value, Map<String, String> values) {
            if (value.contains(START_EXPRESSION)) {
                return replaceExpressions(value, values);
            }
            return value;
        }

        private static String replaceExpressions(String value, Map<String, String> values) {
            StringJoiner joiner = new StringJoiner("");

            String buffer = value;
            int position = buffer.indexOf(START_EXPRESSION);
            while (position >= 0) {
                if (position > 0) {
                    joiner.add(buffer.substring(0, position));
                }
                int endPosition = buffer.indexOf(END_EXPRESSION, position);
                if (endPosition < 0) {
                    throw new RuntimeException("Missing closing brace in expression string \"" + value + "]\"");
                }
                String expression = "";
                if (endPosition - position > 2) {
                    expression = resolve(buffer.substring(position + START_EXPRESSION.length(), endPosition), values);
                }
                joiner.add(expression);
                buffer = buffer.substring(endPosition + END_EXPRESSION.length());
                position = buffer.indexOf(START_EXPRESSION);
            }
            joiner.add(buffer);

            return joiner.toString();

        }

        private static String resolve(String variable, Map<String, String> values) {
            if (values.containsKey(variable)) {
                return values.get(variable);
            }

            return variable;
        }
    }

    private class RequestDeserializer implements JsonDeserializer<Request> {

        @Override
        public Request deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Url.class, new UrlDeserializer());
            gsonBuilder.registerTypeAdapter(String.class, variableSubstitutorThreadLocal.get());

            final Gson gson = gsonBuilder.create();
            Request request = new Request();
            final JsonObject requestJsonObject = json.getAsJsonObject();
            request.setUrl(gson.fromJson(requestJsonObject.get("url"), Url.class));

            if (requestJsonObject.has("header")) {
                addHeaders(request, requestJsonObject);
            }

            if (requestJsonObject.has("body")) {
                request.setBody(gson.fromJson(requestJsonObject.get("body"), Body.class));
            }

            request.setMethod(Method.valueOf(applyVariables(requestJsonObject.get("method").getAsString())));

            return request;
        }

        void addHeaders(Request request, JsonObject requestJsonObject) {
            final JsonElement headerJson = requestJsonObject.get("header");

            if (headerJson.isJsonArray()) {
                final JsonArray headerJsonArray = headerJson.getAsJsonArray();
                final Map<String, String> headers = new HashMap<>();
                headerJsonArray.forEach(header -> {
                    final JsonObject headerJsonObject = header.getAsJsonObject();
                    headers.put(
                            applyVariables(headerJsonObject.get("key").getAsString()),
                            applyVariables(headerJsonObject.get("value").getAsString()));
                });
                request.setHeaders(headers);

            }
        }
    }

    private class PathDeserializer implements JsonDeserializer<Path> {

        @Override
        public Path deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonArray()) {
                final List<String> domain = StreamSupport.stream(json.getAsJsonArray().spliterator(), false)
                        .map(JsonElement::getAsString)
                        .map(CollectionLoader.this::applyVariables)
                        .collect(Collectors.toList());
                return new Path(domain);
            } else {
                return new Path(applyVariables(json.getAsString()));
            }
        }
    }

    private class DomainDeserializer implements JsonDeserializer<Domain> {

        @Override
        public Domain deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            if (json.isJsonArray()) {
                final List<String> domain = StreamSupport.stream(json.getAsJsonArray().spliterator(), false)
                        .map(JsonElement::getAsString)
                        .map(CollectionLoader.this::applyVariables)
                        .collect(Collectors.toList());
                return new Domain(domain);
            } else {
                return new Domain(applyVariables(json.getAsString()));
            }
        }
    }

    private class UrlDeserializer implements JsonDeserializer<Url> {

        @Override
        public Url deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Domain.class, new DomainDeserializer());
            gsonBuilder.registerTypeAdapter(Path.class, new PathDeserializer());
            gsonBuilder.registerTypeAdapter(String.class, variableSubstitutorThreadLocal.get());

            final Gson gson = gsonBuilder.create();

            if (json.isJsonObject()) {
                // It is a CompleteUrl
                final CompleteUrl completeUrl = gson.fromJson(json, CompleteUrl.class);
                return new Url(completeUrl);
            } else {
                try {
                    return new Url(new URL(applyVariables(json.getAsString())));
                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
    }

    private class ItemDeserializer implements JsonDeserializer<Item> {

        @Override
        public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Request.class, new RequestDeserializer());
            gsonBuilder.registerTypeAdapter(String.class, variableSubstitutorThreadLocal.get());
            gsonBuilder.registerTypeAdapter(Item.class, this);

            final Gson gson = gsonBuilder.create();

            JsonObject itemJsonObject = json.getAsJsonObject();
            if (itemJsonObject.has("request")) {
                //Means it is an Item Item
                ItemItem itemItem = new ItemItem();

                if (itemJsonObject.has("name")) {
                    itemItem.setName(applyVariables(itemJsonObject.get("name").getAsString()));
                }

                if (itemJsonObject.has("id")) {
                    itemItem.setId(applyVariables(itemJsonObject.get("id").getAsString()));
                }

                addRequest(gson, itemJsonObject, itemItem);

                return itemItem;
            } else {
                return gson.fromJson(json, Folder.class);
            }
        }

        void addRequest(Gson gson, JsonObject itemJsonObject, ItemItem itemItem) {
            final JsonElement requestJson = itemJsonObject.get("request");
            if (requestJson.isJsonObject()) {
                Request request = gson.fromJson(requestJson, Request.class);
                itemItem.setRequestObject(request);
            } else {
                try {
                    itemItem.setRequest(new URL(applyVariables(requestJson.getAsString())));
                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
    }

}
