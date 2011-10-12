package org.jboss.arquillian.persistence;

import java.util.EnumSet;
import java.util.Set;

public enum SourceType 
{
   XML("xml"),
   XLS("xls"),
   UNSUPPORTED("-none-"), 
   NOT_DEFINED("-undefined-");
   
   private static final EnumSet<SourceType> NOT_REAL_FILE_TYPES = EnumSet.of(UNSUPPORTED, NOT_DEFINED);
   
   private final String matchingFileExtension;

   private SourceType(String matchingFileExtension)
   {
      this.matchingFileExtension = matchingFileExtension;
   }
   
   public static boolean isRealType(SourceType type)
   {
      return !NOT_REAL_FILE_TYPES.contains(type);
   }
   
   public static SourceType inferFromFile(String fileName)
   {
      SourceType result = UNSUPPORTED;
      final Set<SourceType> validSourceTypes = EnumSet.complementOf(NOT_REAL_FILE_TYPES);
      
      for (SourceType type : validSourceTypes)
      {
         if (fileName.endsWith(type.matchingFileExtension))
         {
            return type;
         }
      }
      
      return result;
      
   }
   
}
