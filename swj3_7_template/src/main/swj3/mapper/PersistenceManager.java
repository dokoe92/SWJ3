package swj3.mapper;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PersistenceManager implements AutoCloseable {
    private final String connectionString;
    private final String userName;
    private final String password;
    private Connection connection;

    public PersistenceManager(String connectionString) {
        this(connectionString, null, null);
    }

    public PersistenceManager(String connectionString, String userName, String password) {
        this.connectionString = connectionString;
        this.userName = userName;
        this.password = password;
    }

    private <T> T mapRowToEntity(Class<T> entityType, ResultSet rs) throws ReflectiveOperationException, SQLException {
        T entity = entityType.getDeclaredConstructor().newInstance();
        var builder = new QueryBuilder(entityType);
        for (var fd : builder.getFieldDescriptions()) {
            Field field = entityType.getDeclaredField(fd.getFieldName());
            field.setAccessible(true);
            Object value = rs.getObject(field.getName());
            field.set(entity, value);
        }
        return entity;
    }

    private <T> List<T> mapRowsToEntities(Class<T> entityType, ResultSet resultSet) throws SQLException, ReflectiveOperationException {
        List<T> result = new ArrayList<>();
        while (resultSet.next()) {
            result.add(mapRowToEntity(entityType, resultSet));
        }
        return result;
    }

    public Connection getConnection() {
        try {
            if (connection == null)
                connection = DriverManager.getConnection(connectionString, userName, password);
            return connection;
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't establish connection to database. SQLException: "
                    + ex.getMessage());
        }
    }

    // findAll after refactoring
    public <T> List<T> findAll(Class<T> entityType) {
        var builder = new QueryBuilder(entityType);
        var query = builder.buildSelectAllQuery();

        try (var stmt = getConnection().createStatement(); var resultSet = stmt.executeQuery(query)) {
            return mapRowsToEntities(entityType, resultSet);
        } catch (SQLException | ReflectiveOperationException ex) {
            throw new DataAccessException("Failed to query entities: %s".formatted(ex.getMessage()));
        }
    }

    public <T> Optional<T> findById(Class<T> entityType, Object id) {
        var builder = new QueryBuilder(entityType);
        var query = builder.buildSelectByIdQuery();

        try (var stmt = getConnection().prepareStatement(query)) {
            stmt.setObject(1, id);

            try (var resultSet = stmt.executeQuery()) {
                var result = mapRowsToEntities(entityType, resultSet);
                return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
            }
        } catch (SQLException | ReflectiveOperationException ex) {
            throw new DataAccessException("Failed to fetch entity: %s".formatted(ex.getMessage()));
        }
    }

    public <T> boolean exists(Class<T> entityType, Object id) {
        return false; // TODO
    }

    public <T> List<T> findAllWhere(Class<T> entityType, Predicate<T> filter) {
        return new ArrayList<>(); // TODO
    }

    public <T> void insert(T entity) {
        var builder = new QueryBuilder(entity.getClass());
        var fields = builder.getFieldDescriptions();
        var query = builder.buildInsertQuery();

        try (var stmt = getConnection().prepareStatement(query)) {
            for (int i = 0; i < fields.size(); i++) {
                Field field = entity.getClass().getDeclaredField(fields.get(i).getFieldName());
                field.setAccessible(true);
                stmt.setObject(i + 1, field.get(entity));
            }
            stmt.executeUpdate();
        } catch (SQLException | ReflectiveOperationException ex) {
            throw new DataAccessException("Failed to insert entity: %s".formatted(ex.getMessage()));
        }
    }

    public <T> void insertIfNotExists(Class<T> entityType, Object id, T entity) {
        // TODO
    }

    public <T> void update(T entity) {
        var builder = new QueryBuilder(entity.getClass());
        var fieldDesc = builder.getFieldDescriptions();
        var key = builder.getKeyDescription().getFieldName();

        // Imperative version:
        // List<FieldDescription> nonKeyFields = new ArrayList<>();
        // for (FieldDescription f : fieldDesc) {
        //   if (!f.getFieldName().equals(key)) {
        //     nonKeyFields.add(f);
        //   }
        // }

        var nonKeyFields = fieldDesc.stream()
                .filter(f -> !f.getFieldName().equals(key))
                .toList();

        var query = builder.buildUpdateQuery();

        try (var stmt = getConnection().prepareStatement(query)) {
            Class<?> entityType = entity.getClass();

            for (int i = 0; i < nonKeyFields.size(); i++) {
                Field field = entityType.getDeclaredField(nonKeyFields.get(i).getFieldName());
                field.setAccessible(true);
                stmt.setObject(i + 1, field.get(entity));
            }

            Field keyField = entityType.getDeclaredField(key);
            keyField.setAccessible(true);
            stmt.setObject(nonKeyFields.size() + 1, keyField.get(entity));

            stmt.executeUpdate();
        } catch (SQLException | ReflectiveOperationException ex) {
            throw new DataAccessException("Failed to update entity: %s".formatted(ex.getMessage()));
        }
    }

    public <T> void delete(Class<T> entityType, Object id) {
        var builder = new QueryBuilder(entityType);
        var query = builder.buildDeleteByIdQuery();

        try (var stmt = getConnection().prepareStatement(query)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to delete entity: %s".formatted(ex.getMessage()));
        }
    }

    public <T> void deleteIfExists(Class<T> entityType, Object id) {
        // if (findById(entityType, id).ifPresent()) {
        //   delete(entityType, id);
        // }

        findById(entityType, id).ifPresent(__ -> delete(entityType, id));
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DataAccessException("Problems closing connection: %s".formatted(e.getMessage()));
            }
        }
    }
}
