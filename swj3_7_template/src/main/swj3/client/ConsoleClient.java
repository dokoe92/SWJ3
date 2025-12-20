package swj3.client;

import swj3.domain.Person;
import swj3.mapper.PersistenceManager;
import swj3.mapper.QueryBuilder;

import java.util.List;
import java.util.Optional;

public class ConsoleClient {

    private static final String CONNECTION_STRING = "jdbc:mysql://localhost/phonebook_db?autoReconnect=true&useSSL=false";
    private static final String USER_NAME = "root";
    private static final String PASSWORD = null;

    public static void main(String[] args) {
        System.out.println("----------------------------------------");

        QueryBuilder personQB = new QueryBuilder(Person.class);

        String insertQuery = personQB.buildInsertQuery();
        System.out.printf("insertQuery         = '%s'%n", insertQuery);

        String selectAllQuery = personQB.buildSelectAllQuery();
        System.out.printf("selectAllQuery      = '%s'%n", selectAllQuery);

        String selectByIdQuery = personQB.buildSelectByIdQuery();
        System.out.printf("selectByIdQuery     = '%s'%n", selectByIdQuery);

        String updateQuery = personQB.buildUpdateQuery();
        System.out.printf("updateQuery         = '%s'%n", updateQuery);

        String deleteQuery = personQB.buildDeleteByIdQuery();
        System.out.printf("deleteByIdQuery     = '%s'%n", deleteQuery);

        System.out.println("----------------------------------------");

        try (PersistenceManager pm = new PersistenceManager(CONNECTION_STRING, USER_NAME, PASSWORD)) {
            Person newPerson = new Person(4711, "Franz", "Huber", "Hauptstrasse 1", "(0681) 333 444");

            System.out.printf("Delete person with id '%d' if it exists%n", newPerson.getId());
            pm.deleteIfExists(Person.class, newPerson.getId());

            System.out.printf("Insert person with id '%d'%n", newPerson.getId());
            pm.insert(newPerson);

            System.out.printf("Insert again (should not insert if exists)%n");
            pm.insertIfNotExists(Person.class, newPerson.getId(), newPerson);

            System.out.printf("Load all persons from DB%n");
            pm.findAll(Person.class).forEach(p -> System.out.printf(" --> %s%n", p));

            System.out.printf("Load person with id '%d' from DB%n", newPerson.getId());
            Optional<Person> personLoaded = pm.findById(Person.class, newPerson.getId());
            personLoaded.ifPresent(p -> System.out.printf(" --> %s%n", p));

            System.out.printf("Person with id '%d' exists: %b%n", newPerson.getId(),
                    pm.exists(Person.class, newPerson.getId()));

            System.out.println("Update person (set last name to 'Huber-Smith')");
            newPerson.setLastName("Huber-Smith");
            pm.update(newPerson);

            newPerson.setLastName("Huber-Smith");
            pm.delete(Person.class, newPerson.getId()); // Remove update to reflect actual PersistenceManager methods
            pm.insert(newPerson);

            System.out.println("Load all persons from DB:");
            List<Person> persons = pm.findAll(Person.class);
            persons.forEach(System.out::println);

            System.out.println("Filter persons using findAllWhere with predicate (last name contains 'Smith'):");
            List<Person> smiths = pm.findAllWhere(Person.class, p -> p.getLastName().contains("Smith"));
            smiths.forEach(System.out::println);
        }
    }
}

