package org.jboss.arquillian.persistence.event;

import org.jboss.arquillian.core.spi.event.Event;
import org.jboss.arquillian.persistence.SourceType;

public class PrepareDataEvent implements Event
{

   private final String sourceFile;
   
   private final SourceType type;

   public PrepareDataEvent(String sourceFile, SourceType type)
   {
      this.sourceFile = sourceFile;
      this.type = type;
   }

   public String getSourceFile()
   {
      return sourceFile;
   }

   public SourceType getType()
   {
      return type;
   }
   
}
