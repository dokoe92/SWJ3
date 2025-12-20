package swj3.mapper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryBuilder {
  private final Class<?> entityType;
  private String tableName;  // cached tableName
  private List<FieldDescription> fieldDescriptions; // cached fieldDescrriptions

  public QueryBuilder(Class<?> entityType) {
    this.entityType = entityType;
  }

  public String repeat(String string, int n, String delimiter) {
    // return repeatImperative(string, n, delimiter);
    return repeatFunctional(string, n, delimiter);
  }

  private String repeatImperative(String string, int n, String delimiter) {
    StringJoiner repeatedString = new StringJoiner(delimiter);
    for (int i = 0; i < n; i++) repeatedString.add(string);
    return repeatedString.toString();
  }

  private String repeatFunctional(String string, int n, String delimiter) {
    return Stream.generate(() -> string)
            .limit(n)
            .collect(Collectors.joining(delimiter));
  }

  public String getTableName() {
    return getTableNameImperative();
    // return getTableNameFunctional();
  }

  private String getTableNameImperative() {
    if (tableName == null) {
      Table table = entityType.getAnnotation(Table.class);
      if (table == null) {
        throw new DataAccessException("Entity '%s' not mapped".formatted(entityType.getName()));
      }
      tableName = table.name().isEmpty() ? entityType.getSimpleName() : table.name();
    }
    return tableName;
  }

  private String getTableNameFunctional() {
    if (tableName == null) {
      tableName = Optional.ofNullable(entityType.getAnnotation(Table.class))
              .map(t -> t.name().isEmpty() ? entityType.getSimpleName()
                      : t.name())
              .orElseThrow(() -> new DataAccessException("Entity '%s' not mapped".formatted(entityType.getName())));
    }
    return tableName;
  }

  public List<FieldDescription> getFieldDescriptions() {
//    return getFieldDescriptionsImperative();
     return getFieldDescriptionsFunctional1();
    // return getFieldDescriptionsFunctional2();
  }

  private List<FieldDescription> getFieldDescriptionsImperative() {
    if (fieldDescriptions == null) {
      fieldDescriptions = new ArrayList<FieldDescription>();
      for (var field : entityType.getDeclaredFields()) {
        Column column = field.getAnnotation(Column.class);
        if (column != null) {
          String columnName = field.getName();
          if (!column.name().isEmpty()) {
            columnName = column.name();
          }
          fieldDescriptions.add(new FieldDescription(field.getName(), columnName, column.isKey()));
        }
      }
    }
    return fieldDescriptions;
  }

  private List<FieldDescription> getFieldDescriptionsFunctional1() {
    if (fieldDescriptions == null) {
      fieldDescriptions = List.of(entityType.getDeclaredFields()).stream()
              .filter(field -> field.getAnnotation(Column.class) != null)
              .map(field -> {
                Column column = field.getAnnotation(Column.class);
                String columnName = column.name().isEmpty() ? field.getName() : column.name();
                return new FieldDescription(field.getName(), columnName, column.isKey());
              })
              .collect(Collectors.toList());
    }
    return fieldDescriptions;
  }

  private List<FieldDescription> getFieldDescriptionsFunctional2() {
    if (fieldDescriptions == null) {
      // more efficient, less readable
      fieldDescriptions = Stream.of(entityType.getDeclaredFields())
              .map(field -> {
                Column column = field.getAnnotation(Column.class);
                if (column == null) return null;
                String columnName = column.name().isEmpty() ? field.getName() : column.name();
                return new FieldDescription(field.getName(), columnName, column.isKey());
              })
              .filter(Objects::nonNull)
              .collect(Collectors.toList());
    }
    return fieldDescriptions;
  }

  public FieldDescription getKeyDescription() {
    return getKeyDescriptionImperative();
    // return getKeyDescriptionFunctional1();
    // return getKeyDescriptionFunctional2();
  }

  private FieldDescription getKeyDescriptionImperative() {
    int numKeys = 0;
    FieldDescription key = null;
    for (FieldDescription fd : getFieldDescriptions()) {
      if (fd.isKey()) {
        if (++numKeys > 1) {
          throw new DataAccessException("Entity '%s' has multiple key columns, which is not supported"
                  .formatted(entityType.getSimpleName()));
        }
        key = fd;
      }
    }
    if (numKeys == 0) {
      throw new DataAccessException("Entity '%s' has no key column"
              .formatted(entityType.getSimpleName()));
    }
    return key;
  }

  private FieldDescription getKeyDescriptionFunctional1() {
    return null;
  }

  private FieldDescription getKeyDescriptionFunctional2() {
    // tricky
    return getFieldDescriptions().stream()
            .filter(FieldDescription::isKey)
            .reduce((a, b) -> {
              throw new DataAccessException(
                      "Entity '%s' has multiple key columns, which is not supported"
                              .formatted(entityType.getSimpleName()));
            })
            .orElseThrow(() -> new DataAccessException("Entity '%s' has no key column"
                    .formatted(entityType.getSimpleName())));
  }

  public String getColumnList() {
    return getColumnListImperative();
    // return getColumnListFunctional();
  }

  private String getColumnListImperative() {
    StringBuilder sb = new StringBuilder();
    for (FieldDescription fd : getFieldDescriptions()) {
      if (sb.length() > 0) sb.append(", ");
      sb.append(fd.getColumnName());
    }
    return sb.toString();
  }

  private String getColumnListFunctional() {
    return null;
  }

  public String buildInsertQuery() {
    String questionMarks = repeat("?", getFieldDescriptions().size(), ", ");
    return "insert into %s (%s) values (%s)"
            .formatted(getTableName(), getColumnList(), questionMarks);
  }

  public String buildSelectAllQuery() {
    return "select %s from %s".formatted(getColumnList(), getTableName());
  }

  public String buildSelectByIdQuery() {
    return "%s where %s = ?"
            .formatted(buildSelectAllQuery(), getKeyDescription().getColumnName());
  }

  public String buildUpdateQuery() {
    String setClause = "TODO";
    return "update %s set %s where %s = ?"
            .formatted(getTableName(), setClause, getKeyDescription().getColumnName());
  }

  public String buildDeleteByIdQuery() {
    return "delete from %s where %s = ?"
            .formatted(getTableName(), getKeyDescription().getColumnName());
  }
}
