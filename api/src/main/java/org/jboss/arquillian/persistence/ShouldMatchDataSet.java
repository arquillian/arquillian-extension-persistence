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
package org.jboss.arquillian.persistence;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Verifies state of underlying data store using data sets
 * defined by this annotation. Verification is invoked after test's
 * execution (including transaction if enabled).
 *
 * <br />
 * If files are not specified explicitly, following strategy is applied:
 * <ul>
 *   <li>Assumption that files are stored in <code>datasets</code> folder.</li>
 *   <li>
 *       If annotation is defined on method level, file name has following format:
 *       <i>expected-[fully qualified class name]#[test method name].[default format]</i>.
 *   </li>
 *   <li>
 *       If annotation is defined on class level, file name has following format:
 *       <i>expected-[fully qualified class name].[default format]</i>.
 *   </li>
 * </ul>
 * <br /><br />
 * If not specified in <code>arquillian.xml</code>, then expected format is XML.
 *
 * Presence of this annotation in the test class enables Arquillian Persistence Extension.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Inherited
public @interface ShouldMatchDataSet
{
   /**
    * List of data set files used for comparision
    * @return
    */
   String[] value() default "";

   /**
    * List of columns to be excluded.
    * @return
    */
   String[] excludeColumns() default "";

}
