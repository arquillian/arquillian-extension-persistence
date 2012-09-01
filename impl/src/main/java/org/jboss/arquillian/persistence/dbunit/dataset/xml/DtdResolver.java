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
package org.jboss.arquillian.persistence.dbunit.dataset.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class DtdResolver
{

   /**
    * @param xmlFile
    * @return name of DTD file specified in the !DOCTYPE or null if not specified.
    */
   public String resolveDtdLocation(final String xmlFile)
   {
      final InputStream xmlStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(xmlFile);
      try
      {
         final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
         builder.setEntityResolver(new EntityResolver()
         {
            @Override
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
            {
               // Ignore schema validation at this point - we only need to know where DTD sits
               return new InputSource(new StringReader(""));
            }
         });
         final Document document = builder.parse(xmlStream);
         if (document.getDoctype() == null)
         {
            return null;
         }
         return document.getDoctype().getSystemId();
      }
      catch (Exception e)
      {
         throw new RuntimeException("Unable to resolve dtd for " + xmlFile, e);
      }

   }

   /**
    * @param xmlFile
    * @return name of DTD file specified in the !DOCTYPE with full path inferred from the file location
    *         or null if not specified.
    */
   public String resolveDtdLocationFullPath(final String xml)
   {
      final String dtd = resolveDtdLocation(xml);
      if (dtd == null)
      {
         return null;
      }
      final String path = xml.substring(0, xml.lastIndexOf('/'));
      return path + '/' + dtd;
   }

}
