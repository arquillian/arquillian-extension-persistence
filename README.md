Arquillian Persistence Extension
================================

### What is this?

Arquillian Persistence Extension was created to help you writing tests where persistence layer is involved.
Inspired by great framework called [Unitils](http://unitils.org/), it brings bunch of annotations to help you 
deal with the underlying data storage.

Arquillian Persistence Extension comes with following features:

- Wrapping each test in the seperated transaction (with **commit**(default) or **rollback** at the end).
- Seeding database using [DBUnit](http://dbunit.org) with **XML**, **XLS** and **YAML** supported as data sets format.
- Comparing database state at the end of the test using given data sets.

Currently tested on **Glassfish 3.1 Embedded** and **JBoss AS 7.0.2 Final** (managed).

Enough talking, let's see it in action!

### Code example
    
    @RunWith(Arquillian.class)
    public class UserPersistenceTest
    {

      @Deployment
      public static Archive<?> createDeploymentPackage()
      {
          return ShrinkWrap.create(JavaArchive.class, "test.jar")
                           .addPackage(UserAccount.class.getPackage())
                           .addPackages(true, "org.fest") // FEST Assert is not part of Arquillian JUnit
                           .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                           .addAsManifestResource("test-persistence.xml", "persistence.xml");
      }

      @PersistenceContext
      EntityManager em;
   
      @Test
      @Data("datasets/users.yml")
      @Expected("datasets/expected-users.yml")
      public void shouldChangeUserPassword() throws Exception
      {
          // given
          String expectedPassword = "LexLuthor";
          UserAccount user = em.find(UserAccount.class, 2L);
    
          // when
          user.setPassword("LexLuthor");
          em.merge(user);
      
          // then 
          assertThat(user.getPassword()).isEqualTo(expectedPassword);
      }
    }

There are just two things which are different from the standard Arquillian test - `@Data` and `@Expected` annotations. Former 
seeds the database using file in YAML format, and latter verifies database state using given file. 

This example is taken from **integration tests** written for this project, so feel free to have a closer look. But it's that easy! And there's more to come!

