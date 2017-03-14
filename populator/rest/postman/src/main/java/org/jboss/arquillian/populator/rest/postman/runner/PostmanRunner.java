package org.jboss.arquillian.populator.rest.postman.runner;

import okhttp3.*;
import org.jboss.arquillian.populator.rest.postman.runner.model.*;
import org.jboss.arquillian.populator.rest.postman.runner.model.Request;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PostmanRunner {

    private CollectionLoader collectionLoader = new CollectionLoader();
    private final OkHttpClient client;

    public PostmanRunner() {
        this.client = new OkHttpClient.Builder()
                .addInterceptor(new RetryInterceptor())
                .build();
    }

    public void executeCalls(Map<String, String> variables, String... collectionsLocation) {
        this.executeCalls(new HostPortOverride(), variables, collectionsLocation);
    }

    public void executeCalls(HostPortOverride hostPortOverride, Map<String, String> variables, String... collectionsLocation) {
        for (String collectionLocation : collectionsLocation) {
            try {
                executeCall(hostPortOverride, variables, collectionLocation);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    private void executeCall(HostPortOverride hostPortOverride, Map<String, String> variables, String collectionLocation) throws IOException {
        final Collection collection = collectionLoader.load(collectionLocation, variables);

        final List<ItemItem> items = getAllItemsItem(collection.getItem());

        for (ItemItem itemItem : items) {

            if (itemItem.isRequestAsString()) {
                // Simple URL, so GET to given url.

                URL requestUrl = hostPortOverride == null ? itemItem.getRequest() : hostPortOverride.override(itemItem.getRequest());

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(requestUrl)
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            } else {

                // Complex Request
                final Request requestObject = itemItem.getRequestObject();

                URL requestUrl = hostPortOverride == null ? requestObject.getUrl().asNativeUrl() : hostPortOverride.override(requestObject.getUrl().asNativeUrl());

                final okhttp3.Request.Builder builder = new okhttp3.Request.Builder()
                        .url(requestUrl);

                configureConnection(requestObject.getMethod().name(), builder, requestObject);
                configureHeaders(builder, requestObject);

                Response response = client.newCall(builder.build()).execute();
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            }
        }

    }

    private void configureConnection(String method, okhttp3.Request.Builder builder, Request requestObject) {

        RequestBody requestBody = null;

        final Body body = requestObject.getBody();
        if (body.isBodyWithMode()) {

            switch(body.getMode()) {
                case raw: {
                    requestBody = RequestBody.create(
                            getMediaType(requestObject.getHeaders()),
                            body.getRaw());
                    break;
                }
                case formdata: {
                    final FormBody.Builder formBuilder = new FormBody.Builder();
                    body.getFormdata().stream()
                            .forEach(formParameter -> formBuilder.add(formParameter.getKey(), formParameter.getValue()));
                    requestBody = formBuilder.build();
                    break;
                }
                case urlencoded: {
                    final FormBody.Builder formBuilder = new FormBody.Builder();
                    body.getUrlencoded().stream()
                            .forEach(encodedParameter -> formBuilder.addEncoded(encodedParameter.getKey(), encodedParameter.getValue()));
                    requestBody = formBuilder.build();
                    break;
                }
            }
        }

        // Method is null safe
        builder.method(method, requestBody);
    }

    private MediaType getMediaType(Map<String, String> headers) {
        if (headers.containsKey("Content-Type")) {
            return MediaType.parse(headers.get("Content-Type"));
        }

        return MediaType.parse("application/json");
    }

    private void configureHeaders(okhttp3.Request.Builder builder, Request requestObject) {

        final Map<String, String> headers = requestObject.getHeaders();
        builder.headers(Headers.of(headers));

    }


    private List<ItemItem> getAllItemsItem(List<Item> items) {

        List<ItemItem> itemsItem = new ArrayList<>();

        for (Item item : items) {
            if (item.getItemType() == ItemType.ITEM) {
                itemsItem.add((ItemItem) item);
            } else {
                itemsItem.addAll(getAllItemsItem(((Folder) item).getItem()));
            }
        }

        return itemsItem;
    }

    /**
     * This interceptor is created because some servers opens incoming port before they are able to serve petitions.
     */
    private class RetryInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            final okhttp3.Request request = chain.request();

            // try the request
            Response response = chain.proceed(request);

            int tryCount = 0;
            while (!response.isSuccessful() && tryCount < 20) {
                sleep();
                tryCount++;

                // retry the request
                response = chain.proceed(request);
            }

            // otherwise just pass the original response on
            return response;
        }

        private void sleep() {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                //
            }
        }
    }

}
