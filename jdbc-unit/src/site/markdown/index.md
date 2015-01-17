jdbc-unit
=========

JUnit rule to simplify unit tests with jdbc.

##Sample:

```java
public class JDBCUnitTest {

  @Test
  @JDBC(sql = "/path/to/file.sql")
  public void testJDBCMethod(){
    Connection connection = rule.getConnection();
    // do something with the connection
  }

  @Rule
  public JDBCRule rule = new JDBCRule();
}
```
### Maven usage 

Artifacts are deployed to [Maven Central](http://search.maven.org). To use, drop this in your pom.xml:

```xml
<dependency>
  <groupId>com.github.sdorra</groupId>
  <artifactId>jdbc-unit</artifactId>
  <version>1.0.0</version>
</dependency>
```