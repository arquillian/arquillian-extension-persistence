package org.jboss.arquillian.persistence.data;

import static org.fest.assertions.Assertions.*;

import org.jboss.arquillian.persistence.Format;
import org.junit.Test;

public class DataSetFileNamingStrategyTest
{

   @Test
   public void shouldProduceDefaultFileNameOfDataSetForTestUsingFullClassNameAndMethodName() throws Exception
   {
      // given
      DataSetFileNamingStrategy defaultFileNamingStrategy = new DataSetFileNamingStrategy(Format.XML);

      // when
      String fileName = defaultFileNamingStrategy.createFileName(DummyClass.class, DummyClass.class.getMethod("shouldPass"));

      // then
      assertThat(fileName).isEqualTo("org.jboss.arquillian.persistence.data.DummyClass#shouldPass.xml");
   }
   
}
