package swj3.dal;

import java.util.List;
import java.util.Optional;

// DAO interface for accessing Person table
public interface PersonDao {
    long count() throws DataAccessException;
    Optional<Person> findById(int id) throws DataAccessException;
    List<Person> findByLastName(String lastName) throws DataAccessException;
    List<Person> findAll() throws DataAccessException;
    void deleteById(int id) throws DataAccessException;
    void save(Person p) throws DataAccessException;
    void update(Person p) throws DataAccessException;
}