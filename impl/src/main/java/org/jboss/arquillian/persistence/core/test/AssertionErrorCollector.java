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
package org.jboss.arquillian.persistence.core.test;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects all assertion errors which occurred during test execution
 * to report them back at the end of test lifecycle. This approach
 * allows to run through entire test execution and executed all
 * required phases (like cleaning up database at the end of each
 * persistence test) - so called soft assertion.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class AssertionErrorCollector
{
   private final List<String> assertionErrors = new ArrayList<String>();

   public void collect(AssertionError error)
   {
      collect(error.getMessage());
   }

   public void collect(String errorMessage)
   {
      assertionErrors.add(errorMessage);
   }

   public void report()
   {
      if (assertionErrors.isEmpty())
      {
         return;
      }

      throw new AssertionError(createErrorMessage());
   }

   public int amountOfErrors()
   {
      return assertionErrors.size();
   }

   private String createErrorMessage()
   {
      final StringBuilder builder = new StringBuilder();

      builder.append("Test failed in " + amountOfErrors() + " case" + (amountOfErrors() > 1 ? "s" : "") +  ". \n");
      for (String errorMessage : assertionErrors)
      {
         builder.append(errorMessage).append('\n');
      }
      final String errorMessage = builder.toString();
      return errorMessage;
   }

}
