package org.jboss.arquillian.persistence.data;

import static org.fest.assertions.Assertions.*;

import org.jboss.arquillian.persistence.Format;
import org.junit.Test;

public class ExpectedDataSetFileNamingStrategyTest
{

   @Test
   public void shouldProduceDefaultFileNameOfExpectedDataSetForTestUsingFullClassNameAndMethodName() throws Exception
   {
      // given
      ExpectedDataSetFileNamingStrategy defaultFileNamingStrategy = new ExpectedDataSetFileNamingStrategy(Format.XML);

      // when
      String fileName = defaultFileNamingStrategy.createFileName(DummyClass.class, DummyClass.class.getMethod("shouldPass"));

      // then
      assertThat(fileName).isEqualTo("expected-org.jboss.arquillian.persistence.data.DummyClass#shouldPass.xml");
   }
   
}
