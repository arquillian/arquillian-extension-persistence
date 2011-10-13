package org.jboss.arquillian.persistence.data;

import static org.fest.assertions.Assertions.*;

import org.jboss.arquillian.persistence.Format;
import org.junit.Test;

public class DefaultFileNamingStrategyTest
{

   @Test
   public void shouldProduceDefaultFileNameForTestUsingFullClassNameAndMethodName() throws Exception
   {
      // given
      DefaultFileNamingStrategy defaultFileNamingStrategy = new DefaultFileNamingStrategy(Format.XML);

      // when
      String fileName = defaultFileNamingStrategy.createFileName(DummyClass.class, DummyClass.class.getMethod("shouldPass"));

      // then
      assertThat(fileName).isEqualTo("org.jboss.arquillian.persistence.data.DummyClass#shouldPass.xml");
   }
   
}
