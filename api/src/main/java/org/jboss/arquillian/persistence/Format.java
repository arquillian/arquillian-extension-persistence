package org.jboss.arquillian.persistence;

import java.util.EnumSet;
import java.util.Set;

public enum Format 
{
   XML("xml"),
   XLS("xls"),
   YAML("yml"),
   UNSUPPORTED("-none-"), 
   NOT_DEFINED("-undefined-");
   
   private static final EnumSet<Format> NOT_REAL_FILE_TYPES = EnumSet.of(UNSUPPORTED, NOT_DEFINED);
   
   private final String fileExtension;

   private Format(String fileExtension)
   {
      this.fileExtension = fileExtension;
   }
   
   public String extension()
   {
      return "." + fileExtension;
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
         if (fileName.endsWith(format.fileExtension))
         {
            return format;
         }
      }
      
      return result;
      
   }
   
}
