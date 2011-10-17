package org.jboss.arquillian.persistence.event;

import org.jboss.arquillian.core.spi.event.Event;
import org.jboss.arquillian.persistence.data.Format;

public class PrepareData implements Event
{

   private final String sourceFile;
   
   private final Format format;

   public PrepareData(String sourceFile, Format format)
   {
      this.sourceFile = sourceFile;
      this.format = format;
   }

   public String getSourceFile()
   {
      return sourceFile;
   }

   public Format getFormat()
   {
      return format;
   }
   
}
