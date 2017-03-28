/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
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

package org.arquillian.ape.rdbms.dbunit.api;

import org.arquillian.ape.rdbms.ShouldMatchDataSet;
import org.dbunit.dataset.filter.IColumnFilter;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Provides ability to define custom column filters
 * ({@link org.dbunit.dataset.filter.IColumnFilter}) used when comparing datasets specified by
 * {@link ShouldMatchDataSet} annotation.
 * <br><br>
 * The use of IColumnFilter implementations is described <a href="http://www.dbunit.org/faq.html#columnfilter">here</a>.
 * <br><br>
 * <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Inherited
public @interface CustomColumnFilter {
    /**
     * Custom column filters to be applied in the specified order. Each concrete implementation
     * is expected to have default non-argument constructor which will be used when creating an instance of
     * the filter.
     */
    Class<? extends IColumnFilter>[] value();
}
