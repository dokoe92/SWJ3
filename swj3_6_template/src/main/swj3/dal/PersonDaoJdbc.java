package swj3.dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PersonDaoJdbc implements PersonDao {

    private final String connectionString;
    private final String userName;
    private final String password;

    public PersonDaoJdbc(String connectionString, String userName, String password) {
        this.connectionString = connectionString;
        this.userName = userName;
        this.password = password;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionString, userName, password);
    }

    private void releaseConnection(Connection conn) {
    }

    /**
     * Maps a ResultSet row to a Person object
     */
    private Person mapRow(ResultSet rs) throws SQLException {
        return new Person(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("address"),
                rs.getString("phone_number")
        );
    }

    /**
     * Helper method to query persons with criteria
     */
    protected List<Person> findByCriteria(String criteria, Object... params) throws DataAccessException {
        String sql = "select * from person";
        Connection conn = null;

        if (criteria != null && !criteria.isEmpty()) {
            sql += " where " + criteria;
        }

        try {
            conn = getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }

                try (ResultSet rs = stmt.executeQuery()) {
                    List<Person> result = new ArrayList<>();
                    while (rs.next()) {
                        result.add(mapRow(rs));
                    }
                    return result;
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(
                    "Error querying persons with criteria. SQLException: %s".formatted(ex.getMessage()));
        } finally {

            releaseConnection(conn);

        }
    }

    @Override
    public long count() throws DataAccessException {
        final String SQL = "select count(id) from person";
        Connection conn = null;

        try {
            conn = getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(SQL)) {

                return rs.next() ? rs.getLong(1) : 0;
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error counting persons. SQLException: %s".formatted(ex.getMessage()));
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public Optional<Person> findById(int id) throws DataAccessException {
        List<Person> list = findByCriteria("id = ?", id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    @Override
    public List<Person> findByLastName(String lastName) throws DataAccessException {
        return findByCriteria("last_name like ?", lastName);
    }

    @Override
    public List<Person> findAll() throws DataAccessException {
        return findByCriteria(null);
    }

    @Override
    public void deleteById(int id) throws DataAccessException {
        final String sql = "delete from person where id = ?";
        Connection conn = null;

        try {
            conn = getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(
                    "Error deleting person with id %d. SQLException: %s".formatted(id, ex.getMessage()));
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void save(Person person) throws DataAccessException {
        final String sql = "insert into person (first_name, last_name, address, phone_number) values (?, ?, ?, ?)";
        Connection conn = null;

        if (person.getId() != -1) {
            throw new DataAccessException("Cannot insert an existing person with id %d".formatted(person.getId()));
        }

        try {
            conn = getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, person.getFirstName());
                stmt.setString(2, person.getLastName());
                stmt.setString(3, person.getAddress());
                stmt.setString(4, person.getPhoneNumber());

                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        person.setId(rs.getInt(1));
                    } else {
                        throw new DataAccessException("Failed to retrieve generated id for person");
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error saving person. SQLException: %s".formatted(ex.getMessage()));
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void update(Person person) throws DataAccessException {
        final String sql = "update person set first_name = ?, last_name = ?, address = ?, phone_number = ? where id = ?";
        Connection conn = null;

        if (person.getId() == -1) {
            throw new DataAccessException("Cannot update a non-existing person");
        }

        try {
            conn = getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, person.getFirstName());
                stmt.setString(2, person.getLastName());
                stmt.setString(3, person.getAddress());
                stmt.setString(4, person.getPhoneNumber());
                stmt.setInt(5, person.getId());

                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(
                    "Error updating person with id %d. SQLException: %s".formatted(person.getId(), ex.getMessage()));
        } finally {
            releaseConnection(conn);
        }
    }
}
