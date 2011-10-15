package org.jboss.arquillian.persistence.data.dbunit.dataset.yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.DataSetException;

public class YamlDataSet extends CachedDataSet
{

   public YamlDataSet(YamlDataSetProducer producer, boolean caseSensitiveTableNames) throws DataSetException
   {
      super(producer, caseSensitiveTableNames);
   }
   
   public YamlDataSet(File file, boolean caseSensitiveTableNames) throws DataSetException, FileNotFoundException
   {
      this(new FileInputStream(file), caseSensitiveTableNames);
   }
   
   public YamlDataSet(File file) throws IOException, DataSetException
   {
      this(new FileInputStream(file), false);
   }

   public YamlDataSet(YamlDataSetProducer producer) throws DataSetException
   {
      this(producer, false);
   }

   public YamlDataSet(InputStream inputStream) throws DataSetException
   {    
      this(inputStream, false);
   }
   
   public YamlDataSet(InputStream inputStream, boolean caseSensitiveTableNames) throws DataSetException
   {
      this(new YamlDataSetProducer(inputStream), caseSensitiveTableNames);
   }
   
}
