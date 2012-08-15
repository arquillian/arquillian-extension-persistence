package org.jboss.arquillian.persistence.core.configuration;

import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import org.jboss.arquillian.container.test.api.Testable;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.ArchiveAsset;

public class PersistenceDescriptorArchiveExtractor
{

   private static final String WAR_AND_JAR = ".*\\.war|.*\\.jar";

   /**
    * Returns open stream of persistence.xml found in the archive, but
    * only if single file have been found.
    *
    * @param archive
    * @return Input stream of persistence.xml found or null if zero or multiple found in the archive.
    */
   public InputStream getAsStream(final Archive<?> archive)
   {
      final Archive<?> testable = findTestableArchive(archive);
      final Collection<Node> values = collectPersistenceXml(testable);
      if (values.size() == 1)
      {
         return values.iterator().next().getAsset().openStream();
      }

      return null;
   }
   /**
    * Inspects archive in order to find nested testable archive, assuming
    * @param archive
    * @return testable archive or passed one if nothing found
    */
   private Archive<?> findTestableArchive(final Archive<?> archive)
   {
      final Map<ArchivePath, Node> nestedArchives = archive.getContent(Filters.include(WAR_AND_JAR));
      if (!nestedArchives.isEmpty())
      {
         for (ArchivePath path : nestedArchives.keySet())
         {
            try
            {
               GenericArchive genericArchive = archive.getAsType(GenericArchive.class, path);
               if (Testable.isArchiveToTest(genericArchive))
               {
                  return genericArchive;
               }
            }
            catch (IllegalArgumentException e)
            {
               // no-op, Nested archive is not a ShrinkWrap archive.
            }
         }
      }

      return archive;
   }

   /**
    * Recursively scans archive content (including sub archives) for persistence.xml descriptors.
    *
    * @param archive
    * @return
    */
   private Collection<Node> collectPersistenceXml(final Archive<?> archive)
   {
      final Collection<Node> nodes = new LinkedList<Node>(getPersistenceDescriptors(archive));
      for (Node node : collectSubArchives(archive))
      {
         if (node.getAsset() instanceof ArchiveAsset)
         {
            final ArchiveAsset archiveAsset = (ArchiveAsset) node.getAsset();
            nodes.addAll(collectPersistenceXml(archiveAsset.getArchive()));
         }
      }
      return nodes;
   }

   private Collection<Node> getPersistenceDescriptors(final Archive<?> archive)
   {
      return archive.getContent(Filters.include(".*persistence.xml")).values();
   }

   private Collection<Node> collectSubArchives(final Archive<?> archive)
   {
      return archive.getContent(Filters.include(WAR_AND_JAR)).values();
   }
}
