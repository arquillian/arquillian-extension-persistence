package org.jboss.arquillian.persistence.script;

public class SpecialCharactersReplacer
{

   public String escape(String source)
   {
      return source.replaceAll("(?m)&(.[a-zA-Z0-9]*);", "ape_special[$1]");
   }

   public String unescape(String source)
   {
      return source.replaceAll("(?m)ape_special\\[(.[a-zA-Z0-9]*)]", "&$1;") ;
   }
}
