package swj3.simpledal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;

public class PhoneBookApplication {

    private static final String CONNECTION_STRING = "jdbc:mysql://localhost/phonebook_db?autoReconnect=true&useSSL=false";
    private static final String USER_NAME = "root";
    private static final String PASSWORD = null;

    private static String promptFor(BufferedReader in, String p) {
        System.out.print(p + "> ");

        try {
            return in.readLine();
        } catch (Exception e) {
            return promptFor(in, p);
        } // try/catch
    } // prompt

    private static void printStatistics(Connection connection) throws SQLException {
        final String SQL = "select count(id) as count from person";

        try (Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(SQL)) {
            int count = 0;
            if (resultSet.next()) {
                count = resultSet.getInt(1); // NOTE: start at 1 and not 0!!!
                System.out.println();
                System.out.printf("%d entries and phone book.%n", count);
            }
        }
    }

    private static void insert(Connection connection, String firstName,
                               String lastName, String address,
                               String phoneNumber) throws SQLException {

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(
                    ("insert into person (first_name, last_name, address, phone_number) " +
                            "values ('%s','%s','%s','%s')")
                            .formatted(firstName, lastName, address, phoneNumber));
        }
    }

    private static void list(Connection connection) throws SQLException {
        final String SQL = "select * from person";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SQL)) {
            while (resultSet.next()) {
                System.out.printf("  (%s): %s %s; %s; %s%n", resultSet.getInt("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("address"),
                        resultSet.getString("phone_number"));
            } // while
        }
    }

    private static void find(Connection connection, String lastNamePattern) throws SQLException {
        final String SQL = "select * from person where last_name like ?";


        try (PreparedStatement findStatement = connection.prepareStatement(SQL)) {
            // USE '%' wildcards for LIKE pattern
            findStatement.setString(1,'%' + lastNamePattern + '%');

            try (ResultSet resultSet = findStatement.executeQuery()) {
                int count = 0;
                while (resultSet.next()) {
                    count++;
                    System.out.printf(" (%s): %s %s; %s; %s%n",
                            resultSet.getInt("ID"),
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name"),
                            resultSet.getString("address"),
                            resultSet.getString("phone_number"));
                }

                if (count == 0) {
                    System.out.printf("no entries with last name %s found%n", lastNamePattern);
                }
            }

        }
    }

    private static void update(Connection connection, int id, String firstName, String lastName,
                               String address, String phoneNumber) throws SQLException {
        final String SQL = "update person set first_name = ?, last_name = ?, address = ?, phone_number = ? where id = ?";

        try (PreparedStatement prepUpdateStmt = connection.prepareStatement(SQL)) {
            prepUpdateStmt.setString(1, firstName);
            prepUpdateStmt.setString(2, lastName);
            prepUpdateStmt.setString(3, address);
            prepUpdateStmt.setString(4, phoneNumber);
            prepUpdateStmt.setInt(5, id);

            prepUpdateStmt.executeUpdate();
        }
    }

    private static void delete(Connection connection, int id) throws SQLException {
        final String SQL = "delete from person where id = ?";

        try (PreparedStatement prepDeleteStmt = connection.prepareStatement(SQL)) {
            prepDeleteStmt.setInt(1, id);
            prepDeleteStmt.executeUpdate();
        }
    }

    private static void meta(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            DatabaseMetaData dbmd = connection.getMetaData();
            System.out.printf(
                    "Database: %s, %s%n", dbmd.getDatabaseProductName(), dbmd.getDatabaseProductVersion());
            System.out.printf("Driver: %s, %s%n", dbmd.getDriverName(), dbmd.getDriverVersion());

            ResultSet resultSet = statement.executeQuery("select * from person");
            ResultSetMetaData rsmd = resultSet.getMetaData();
            System.out.println("Metainfo for table Person:");
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                System.out
                        .printf("   column %s (%s)%n", rsmd.getColumnName(i), rsmd.getColumnTypeName(i));
            }
        }
    }

    public static void main(String[] args) {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String validCommands = "commands: quit, list, find, insert, update, delete, meta";
        String command;

        System.out.printf("Connecting to \"%s\" ...%n", CONNECTION_STRING);
        long startTime = System.nanoTime();

        try (Connection connection = DriverManager.getConnection(CONNECTION_STRING, USER_NAME, PASSWORD);) {
            long endTime = System.nanoTime();
            System.out.printf("time(DriverManager.getConnection)=%.6f%n", (endTime - startTime) / 1e9);

            printStatistics(connection);

            System.out.println();
            System.out.println(validCommands);

            command = promptFor(in, "");

            while (!command.equals("quit")) {
                int id;
                String firstName;
                String lastName;
                String address;
                String phoneNumber;

                switch (command) {
                    case "list" -> list(connection);

                    case "find" -> {
                        String lastNamePattern = promptFor(in, "  last name ");
                        find(connection, lastNamePattern);
                    }

                    case "insert" -> {
                        firstName = promptFor(in, "  first name   ");
                        lastName = promptFor(in, "  last name    ");
                        address = promptFor(in, "  address      ");
                        phoneNumber = promptFor(in, "  phone number ");

                        insert(connection, firstName, lastName, address, phoneNumber);
                    }

                    case "update" -> {
                        id = Integer.parseInt(promptFor(in, "  ID           "));
                        firstName = promptFor(in, "  first name   ");
                        lastName = promptFor(in, "  last name    ");
                        address = promptFor(in, "  address      ");
                        phoneNumber = promptFor(in, "  phone number ");

                        update(connection, id, firstName, lastName, address, phoneNumber);
                    }

                    case "delete" -> {
                        id = Integer.parseInt(promptFor(in, "  ID "));
                        delete(connection, id);
                    }

                    case "meta" -> meta(connection);

                    default -> {
                        System.out.printf("ERROR: invalid command \"%s\"%n", command);
                        System.out.printf("Valid commands: %s%n", validCommands);
                    }

                } // switch

                command = promptFor(in, "");
            } // while
        } catch (SQLException e) {
            while (e != null) {
                System.out.printf("ERROR: %s%n", e.getMessage());
                e = e.getNextException();
            }
        } finally {
            System.out.println();
            System.out.printf("Closing connection to %s...%n", CONNECTION_STRING);
        }

    } // main
} // PhoneBookApplication
