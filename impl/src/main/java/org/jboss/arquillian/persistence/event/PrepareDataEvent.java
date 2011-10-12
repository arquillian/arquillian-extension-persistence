package org.jboss.arquillian.persistence.event;

import org.jboss.arquillian.core.spi.event.Event;
import org.jboss.arquillian.persistence.Format;

public class PrepareDataEvent implements Event
{

   private final String sourceFile;
   
   private final Format format;

   public PrepareDataEvent(String sourceFile, Format format)
   {
      this.sourceFile = sourceFile;
      this.format = format;
   }

   public String getSourceFile()
   {
      return sourceFile;
   }

   public Format getType()
   {
      return format;
   }
   
}
