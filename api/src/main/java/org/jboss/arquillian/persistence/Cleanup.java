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
 * Determines when database cleanup should be triggered. Default test phase when
 * is {@link TestExecutionPhase#AFTER}. You can set it also globally in <code>arquillian.xml</code>.
 *
 * If not specified otherwise the whole database is erased.
 * You can change this behaviour by setting up {@link #strategy()} field.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 * @see {@link TestExecutionPhase}, {@link CleanupStrategy}
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Inherited
public @interface Cleanup
{

   TestExecutionPhase phase() default TestExecutionPhase.DEFAULT;

   CleanupStrategy strategy() default CleanupStrategy.STRICT;
}
