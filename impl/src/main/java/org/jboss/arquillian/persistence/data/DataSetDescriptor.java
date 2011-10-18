package org.jboss.arquillian.persistence.data;

public class DataSetDescriptor
{

   private final String fileName;
   
   private final Format format;

   public DataSetDescriptor(String fileName, Format format)
   {
      this.fileName = fileName;
      this.format = format;
   }

   public String getFileName()
   {
      return fileName;
   }
   
   public Format getFormat()
   {
      return format;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
      {
         return true;
      }
      if (!(obj instanceof DataSetDescriptor))
      {
         return false;
      }
      
      final DataSetDescriptor other = (DataSetDescriptor) obj;
      return fileName.equals(other.fileName) && format.equals(other.format);
   }
   
   @Override
   public int hashCode()
   {
      final int prime = 17;
      int result = 1;
      result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
      result = prime * result + ((format == null) ? 0 : format.hashCode());
      return result;
   }
   
}
