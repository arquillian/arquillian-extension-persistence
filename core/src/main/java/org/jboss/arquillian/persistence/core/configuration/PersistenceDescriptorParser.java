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
package org.jboss.arquillian.persistence.core.configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilderFactory;

import org.jboss.arquillian.persistence.core.exception.MultiplePersistenceUnitsException;
import org.jboss.arquillian.persistence.core.exception.PersistenceDescriptorParsingException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class PersistenceDescriptorParser
{

   private static final String JTA_DATA_SOURCE_TAG = "jta-data-source";

   private static final String NON_JTA_DATA_SOURCE_TAG = "non-jta-data-source";

   private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

   public String obtainDataSourceName(final String descriptor)
   {
      Document document = parseXmlDescriptor(descriptor);
      final NodeList persistenceUnits = document.getElementsByTagName("persistence-unit");
      if (persistenceUnits.getLength() > 1)
      {
         throw new MultiplePersistenceUnitsException("Multiple persistence units defined. Please specify default data source either in 'arquillian.xml' or by using @DataSource annotation");
      }
      final Node persistenceUnit = persistenceUnits.item(0);
      Node dataSource = getJtaDataSource(persistenceUnit);
      if (dataSource == null)
      {
         dataSource = getNonJtaDataSource(persistenceUnit);
      }
      return dataSource.getTextContent();
   }

   public String obtainDataSourceName(final InputStream inputStream)
   {
      return obtainDataSourceName(new Scanner(inputStream).useDelimiter("\\A").next());
   }

   private Document parseXmlDescriptor(final String descriptor)
   {
      try
      {
         return factory.newDocumentBuilder().parse(new ByteArrayInputStream(descriptor.getBytes()));
      }
      catch (Exception e)
      {
         throw new PersistenceDescriptorParsingException("Unable to parse descriptor " + descriptor, e);
      }
   }
   
   private Node getNonJtaDataSource(Node persistenceUnit)
   {
      return ((Element) persistenceUnit).getElementsByTagName(NON_JTA_DATA_SOURCE_TAG).item(0);
   }

   private Node getJtaDataSource(final Node persistenceUnit)
   {
      return ((Element) persistenceUnit).getElementsByTagName(JTA_DATA_SOURCE_TAG).item(0);
   }

}
