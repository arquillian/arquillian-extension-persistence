/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.arquillian.ape.rdbms.core.configuration;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import net.jcip.annotations.NotThreadSafe;
import org.arquillian.ape.rdbms.core.dbunit.data.descriptor.Format;
import org.assertj.core.util.Arrays;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URI;
import java.net.URL;

import static junitparams.JUnitParamsRunner.$;
import static org.assertj.core.api.Assertions.assertThat;

@NotThreadSafe
@RunWith(JUnitParamsRunner.class)
public class ConfigurationTypeConverterTest {

    @Test
    public void should_convert_empty_string_to_empty_string_array() throws Exception {
        // given
        ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();

        // when
        String[] convertedStringArray = typeConverter.convert("", String[].class);

        // then
        assertThat(convertedStringArray).isEmpty();
    }

    @Test
    public void should_convert_blank_string_to_empty_string_array() throws Exception {
        // given
        ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();

        // when
        String[] convertedStringArray = typeConverter.convert("        ", String[].class);

        // then
        assertThat(convertedStringArray).isEmpty();
    }

    @Test
    public void should_convert_sequence_of_blank_strings_to_empty_string_array() throws Exception {
        // given
        ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();
        String[] arrayOfEmptyStrings = Arrays.array("", "", "", "", "");

        // when
        String[] convertedStringArray = typeConverter.convert(" ,   ,   ,   ,       ", String[].class);

        // then
        assertThat(convertedStringArray).isEqualTo(arrayOfEmptyStrings);
    }

    @Test
    public void should_convert_single_element_to_one_element_array() throws Exception {
        // given
        ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();
        String[] singleElementArray = Arrays.array("one element");

        // when
        String[] convertedStringArray = typeConverter.convert("one element", String[].class);

        // then
        assertThat(convertedStringArray).isEqualTo(singleElementArray);
    }

    @Test
    public void should_convert_single_element_with_delimiter_to_one_element_array() throws Exception {
        // given
        ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();
        String[] singleElementArray = Arrays.array("one element");

        // when
        String[] convertedStringArray = typeConverter.convert("one element,", String[].class);

        // then
        assertThat(convertedStringArray).isEqualTo(singleElementArray);
    }

    @Test
    public void should_convert_single_element_with_delimiters_to_one_element_array() throws Exception {
        // given
        ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();
        String[] singleElementArray = Arrays.array("one element");

        // when
        String[] convertedStringArray = typeConverter.convert("one element,,,,,,,", String[].class);

        // then
        assertThat(convertedStringArray).isEqualTo(singleElementArray);
    }

    @Test
    public void should_convert_blank_to_empty_string_when_appear_in_sequence_with_non_blanks() throws Exception {
        // given
        ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();
        String[] expectedArray = Arrays.array("a", "", "test", "", "", "b");
        // when
        String[] convertedStringArray = typeConverter.convert("a,   , test  ,   ,      , b ", String[].class);

        // then
        assertThat(convertedStringArray).isEqualTo(expectedArray);
    }

    @Test
    public void should_convert_transactional_mode_to_corresponding_enum() throws Exception {
        // given
        TransactionMode expectedMode = TransactionMode.COMMIT;
        ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();

        // when
        TransactionMode convertedMode = typeConverter.convert("COMMIT", TransactionMode.class);

        // then
        assertThat(convertedMode).isEqualTo(expectedMode);
    }

    @Test
    public void should_convert_transactional_mode_to_corresponding_enum_ignoring_case() throws Exception {
        // given
        TransactionMode expectedMode = TransactionMode.DISABLED;
        ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();

        // when
        TransactionMode convertedMode = typeConverter.convert("disabled", TransactionMode.class);

        // then
        assertThat(convertedMode).isEqualTo(expectedMode);
    }

    @Test
    public void should_convert_transactional_mode_to_corresponding_enum_ignoring_case_and_whitespaces() throws Exception {
        // given
        TransactionMode expectedMode = TransactionMode.ROLLBACK;
        ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();

        // when
        TransactionMode convertedMode = typeConverter.convert("  rollBACK  ", TransactionMode.class);

        // then
        assertThat(convertedMode).isEqualTo(expectedMode);
    }

    @Test
    public void should_convert_format_to_corresponding_enum() throws Exception {
        // given
        Format expectedFormat = Format.EXCEL;
        ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();

        // when
        Format convertedFormat = typeConverter.convert("EXCEL", Format.class);

        // then
        assertThat(convertedFormat).isEqualTo(expectedFormat);
    }

    @Test
    public void should_convert_format_to_corresponding_enum_ignoring_case() throws Exception {
        // given
        Format expectedFormat = Format.JSON;
        ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();

        // when
        Format convertedFormat = typeConverter.convert("JsOn", Format.class);

        // then
        assertThat(convertedFormat).isEqualTo(expectedFormat);
    }

    @Test
    public void should_convert_format_to_corresponding_enum_with_enum_name_as_prefix() throws Exception {
        // given
        Format expectedFormat = Format.JSON;
        ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();

        // when
        Format convertedFormat = typeConverter.convert("Format.JSON", Format.class);

        // then
        assertThat(convertedFormat).isEqualTo(expectedFormat);
    }

    @Test
    public void should_convert_format_to_corresponding_enum_with_fully_qualified_enum_name_as_prefix() throws Exception {
        // given
        Format expectedFormat = Format.JSON;
        ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();

        // when
        Format convertedFormat = typeConverter.convert("org.arquillian.persistence.core.data.descriptor.Format.JSON", Format.class);

        // then
        assertThat(convertedFormat).isEqualTo(expectedFormat);
    }

    @Test
    public void should_convert_format_to_corresponding_enum_ignoring_case_and_whitespaces() throws Exception {
        // given
        Format expectedFormat = Format.UNSUPPORTED;
        ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();

        // when
        Format convertedFormat = typeConverter.convert("  unSUPPORTED     ", Format.class);

        // then
        assertThat(convertedFormat).isEqualTo(expectedFormat);
    }

    @Test
    public void should_convert_string() throws Exception {
        // given
        String expectedString = "Hello";
        ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();

        // when
        String convertedString = typeConverter.convert("Hello", String.class);

        // then
        assertThat(convertedString).isEqualTo(expectedString);
    }

    @Test
    public void should_convert_string_to_integer() throws Exception {
        // given
        Integer expectedInteger = Integer.valueOf(15);
        ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();

        // when
        Integer convertedInteger = typeConverter.convert("15", Integer.class);

        // then
        assertThat(convertedInteger).isEqualTo(expectedInteger);
    }

    @Test
    public void should_convert_string_to_double() throws Exception {
        // given
        Double expecteDouble = Double.valueOf("123");
        ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();

        // when
        Double convertedDouble = typeConverter.convert("123", Double.class);

        // then
        assertThat(convertedDouble).isEqualTo(expecteDouble);
    }

    @Test
    public void should_convert_string_to_long() throws Exception {
        // given
        Long expectedLong = Long.valueOf(-456);
        ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();

        // when
        Long convertedLong = typeConverter.convert("-456", Long.class);

        // then
        assertThat(convertedLong).isEqualTo(expectedLong);
    }

    @Test
    public void should_convert_string_to_boolean() throws Exception {
        // given
        ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();

        // when
        Boolean convertedBoolen = typeConverter.convert("True", Boolean.class);

        // then
        assertThat(convertedBoolen).isTrue();
    }

    @Test
    public void should_convert_string_to_URL() throws Exception {
        // given
        URL expectedUrl = new URI("http://www.arquillian.org").toURL();
        ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();

        // when
        URL convertedUrl = typeConverter.convert("http://www.arquillian.org", URL.class);

        // then
        assertThat(convertedUrl).isEqualTo(expectedUrl);
    }

    @Test
    public void should_convert_string_to_URI() throws Exception {
        // given
        URI expectedUri = new URI("http://www.arquillian.org");
        ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();

        // when
        URI convertedUri = typeConverter.convert("http://www.arquillian.org", URI.class);

        // then
        assertThat(convertedUri).isEqualTo(expectedUri);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_for_unsupported_type() throws Exception {
        // given
        ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();

        // when
        typeConverter.convert("typeConverter", ConfigurationTypeConverter.class);

        // then
        // exception should be thrown
    }

    @Test
    public void should_not_box_non_primitive_class() throws Exception {
        // given
        ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();

        // when
        Class<?> boxed = typeConverter.box(Integer.class);

        // then
        assertThat(boxed).isEqualTo(Integer.class);
    }

    @Test
    @Parameters(method = "primitivesToBeBoxed")
    public void should_box_primitive_types(Class<?> primitiveClass, Class<?> expectedBoxedClass) throws Exception {
        // given
        ConfigurationTypeConverter typeConverter = new ConfigurationTypeConverter();

        // when
        Class<?> actualBoxedClass = typeConverter.box(primitiveClass);

        // then
        assertThat(actualBoxedClass).isEqualTo(expectedBoxedClass);
    }

    // -------------------------------------------------------------------------------------------

    @SuppressWarnings("unused") // used by @Parameters
    private Object[] primitivesToBeBoxed() {
        return $(
                $(int.class, Integer.class),
                $(long.class, Long.class),
                $(float.class, Float.class),
                $(double.class, Double.class),
                $(byte.class, Byte.class),
                $(short.class, Short.class),
                $(char.class, Character.class),
                $(boolean.class, Boolean.class)
        );
    }

}
