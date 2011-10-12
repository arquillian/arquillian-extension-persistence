package org.jboss.arquillian.persistence;

import java.util.EnumSet;
import java.util.Set;

public enum Format 
{
   XML("xml"),
   XLS("xls"),
   UNSUPPORTED("-none-"), 
   NOT_DEFINED("-undefined-");
   
   private static final EnumSet<Format> NOT_REAL_FILE_TYPES = EnumSet.of(UNSUPPORTED, NOT_DEFINED);
   
   private final String matchingFileExtension;

   private Format(String matchingFileExtension)
   {
      this.matchingFileExtension = matchingFileExtension;
   }
   
   public static boolean isSupported(Format format)
   {
      return !NOT_REAL_FILE_TYPES.contains(format);
   }
   
   public static Format inferFromFile(String fileName)
   {
      Format result = UNSUPPORTED;
      final Set<Format> validFormats = EnumSet.complementOf(NOT_REAL_FILE_TYPES);
      
      for (Format format : validFormats)
      {
         if (fileName.endsWith(format.matchingFileExtension))
         {
            return format;
         }
      }
      
      return result;
      
   }
   
}
