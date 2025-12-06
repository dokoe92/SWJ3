package swj3.dal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Optional;

public class PhoneBookCLI {

    private static final String CONNECTION_STRING = "jdbc:mysql://localhost/phonebook_db?autoReconnect=true&useSSL=false";
    private static final String USER_NAME = "root";
    private static final String PASSWORD = null;
    private static final int POOL_SIZE = 3;

    public static String promptFor(BufferedReader in, String p) {
        System.out.print(p + "> ");
        try {
            return in.readLine();
        } catch (Exception e) {
            return promptFor(in, p);
        } // try/catch
    } // prompt

    public static void main(String[] args) {

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String validCommands = "quit, list, find, insert, update, delete";
        String command;

        try {
//            PersonDao personDao =
            SimpleConnectionPool pool = new SimpleConnectionPool(CONNECTION_STRING, USER_NAME, PASSWORD, POOL_SIZE);
            PersonDao personDao = new PersonDaoJdbc(pool);

            System.out.println();
            System.out.println("currently " + personDao.count() + " entries in phone book");

            System.out.println();
            System.out.printf("Valid commands: %s%n", validCommands);

            command = promptFor(in, "");

            while (!command.equals("quit")) {

                int id;
                String lastName;
                Person person;

                switch (command) {

                    case "list" -> {
                        for (Person p : personDao.findAll()) {
                            System.out.println(p);
                        }
                    }

                    case "find" -> {
                        lastName = promptFor(in, "  last name ");
                        Collection<Person> persons = personDao.findByLastName(lastName);
                        for (Person p : persons)
                            System.out.println(p);
                        if (persons.isEmpty()) System.out.println("  no entries with last name " + lastName + " found");
                    }

                    case "insert" -> {
                        person = new Person(promptFor(in, "  first name   "), promptFor(in, "  last name    "),
                                promptFor(in, "  address      "), promptFor(in, "  phone number "));
                        personDao.save(person);
                        System.out.printf("inserted new person <%s>%n", person);
                    }

                    case "update" -> {
                        id = Integer.parseInt(promptFor(in, "  id "));
                        Optional<Person> optionalPerson = personDao.findById(id);

                        optionalPerson.ifPresentOrElse(p -> {
                            System.out.println("  " + p);
                            p.setAddress(promptFor(in, "  new address "));
                            personDao.update(p);
                        }, () -> System.out.println("  no entry with id " + id));
                    }

                    case "delete" -> {
                        id = Integer.parseInt(promptFor(in, "  id "));
                        personDao.deleteById(id);
                    }

                    default -> {
                        System.out.printf("ERROR: invalid command \"%s\"%n", command);
                        System.out.printf("Valid commands: %s%n", validCommands);
                    }
                } // switch

                command = promptFor(in, "");

            } // while
            System.out.println();
        } catch (Exception e) {
            System.out.printf("ERROR: %s%n", e.getMessage());
        } // catch
    } // main
} // PhoneBookCLI

