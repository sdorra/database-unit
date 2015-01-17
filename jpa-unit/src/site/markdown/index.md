jpa-unit
=========

JPA rule to simplify unit tests with jpa.

##Sample:

```java
public class JPAUnitTest {

  @Test
  @JPA(value = "test-persistence-unit", sql = "/path/to/file.sql")
  public void testJPAMethod(){
    EntityManager em = rule.getEntityManager();
    // do something with the EntityManager
  }

  @Rule
  public JPARule rule = new JPARule();
}
```
### Maven usage 

Artifacts are deployed to [Maven Central](http://search.maven.org). To use, drop this in your pom.xml: 

```xml
<dependency>
  <groupId>com.github.sdorra</groupId>
  <artifactId>jpa-unit</artifactId>
  <version>1.0.0</version>
</dependency>
```