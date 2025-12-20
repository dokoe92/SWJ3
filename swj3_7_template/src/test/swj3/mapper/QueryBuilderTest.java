package swj3.mapper;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Table(name = "person")
class TestPerson {
    @Column(name = "id", isKey = true)
    private int id;
    @Column(name = "name")
    private String name;
}

public class QueryBuilderTest {

  @Test
  void getTableName_ReturnsPerson() {
    QueryBuilder builder   = new QueryBuilder(TestPerson.class);
    String       tableName = builder.getTableName();
    assertEquals("person", tableName);
  }

  @Test
  void getfieldDescrriptions_ReturnsNonEmptyListWithIdField() {
    QueryBuilder           builder = new QueryBuilder(TestPerson.class);
    List<FieldDescription> fields  = builder.getFieldDescriptions();
    assertFalse(fields.isEmpty());
    assertTrue(fields.stream().anyMatch(f -> f.getFieldName().equals("id")));
  }

  @Test
  void getKeyDescription_ReturnsIdKeyField() {
    QueryBuilder     builder = new QueryBuilder(TestPerson.class);
    FieldDescription key     = builder.getKeyDescription();
    assertNotNull(key);
    assertTrue(key.isKey());
    assertEquals("id", key.getFieldName());
  }

  @Test
  void buildInsertQuery_ReturnsInsertQuery() {
    QueryBuilder builder = new QueryBuilder(TestPerson.class);
    String       query   = builder.buildInsertQuery();
    assertTrue(query.startsWith("insert into person"));
    assertTrue(query.contains("values"));
    assertTrue(Stream.of("id", "name").allMatch(query::contains));
  }

  @Test
  void buildSelectByIdQuery_ReturnsSelectQuery() {
    QueryBuilder builder = new QueryBuilder(TestPerson.class);
    String       query   = builder.buildSelectByIdQuery();
    assertTrue(query.startsWith("select"));
    assertTrue(query.contains("where"));
    assertTrue(Stream.of("id", "name").allMatch(query::contains));
    assertTrue(query.substring(query.indexOf("where")).contains("id"));
  }

  @Test
  void buildUpdateQuery_ReturnsUpdateQuery() {
    QueryBuilder builder = new QueryBuilder(TestPerson.class);
    String       query   = builder.buildUpdateQuery();
    assertTrue(query.startsWith("update person"));
    assertTrue(query.contains("set"));
    assertTrue(query.contains("where"));
    assertTrue(Stream.of("id", "name").allMatch(query::contains));
    assertTrue(query.substring(query.indexOf("where")).contains("id"));
  }

  @Test
  void buildDeleteByIdQuery_ReturnsDeleteQuery() {
    QueryBuilder builder = new QueryBuilder(TestPerson.class);
    String       query   = builder.buildDeleteByIdQuery();
    assertTrue(query.startsWith("delete from person"));
    assertTrue(query.contains("where"));
    assertTrue(query.contains("id"));
    assertTrue(query.substring(query.indexOf("where")).contains("id"));
  }
}
