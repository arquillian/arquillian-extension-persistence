## Arquillian Persistence Extension

### What is this?

**Arquillian Persistence Extension** was created to help you writing tests where persistence layer is involved.
Inspired by great framework called [Unitils](http://unitils.org/), it brings bunch of annotations to help you
deal with the underlying data storage.

It comes with following features:

* Wrapping each test in the separated transaction (with **commit**(default) or **rollback** at the end).
* Seeding database using:
    * [DBUnit](http://dbunit.org) with **XML**, **XLS**, **YAML**  and **JSON** supported as data sets format.
    * Custom SQL scripts.
* Comparing database state at the end of the test using given data sets (with column exclusion).
* Eviction JPA second level cache between test method invocation, see `@JpaCacheEviction`.

##### Containers used for testing
- Glassfish 3.1.2 Embedded
- JBoss AS 7.0.2 Final (managed) 
- JBoss AS 7.1.1.Final (managed)

##### Verified with following databases
- HSQL
- MS SQL 2008 Express (with Microsoft JDBC Driver)
- MySQL 5.5.24
- PostgreSQL 9.1.4
- Derby

Enough talking, let's see it in action!

### Code example
---

```java
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
  @UsingDataSet("datasets/users.yml")
  @ShouldMatchDataSet("datasets/expected-users.yml")
  public void should_change_user_password() throws Exception
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
```

There are just two things which are different from the standard Arquillian test - `@UsingDataSet` and `@ShouldMatchDataSet` annotations. Former
seeds the database using file in YAML format, and latter verifies database state using given file.

This example is taken from **integration tests** written for this project, so feel free to have a closer look. 

But it's that easy! And there's more to come!

### How to reach us out?

If you have any questions or would like to file feature request or bug report (hope not!) please have a look at [the ways how you can get in touch with us](http://arquillian.org/community/).
