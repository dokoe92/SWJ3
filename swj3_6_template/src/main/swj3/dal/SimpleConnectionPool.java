package swj3.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SimpleConnectionPool {

    private final List<Connection> pool = new ArrayList<>();
    private final String url, user, pass;

    public SimpleConnectionPool(String url, String user, String password, int size) throws SQLException {
        this.url = url;
        this.user = user;
        this.pass = password;
        for (int i = 0; i < size; i++) {
            pool.add(DriverManager.getConnection(url, user, password));
        }
    }

    public synchronized Connection getConnection() {
        if (!pool.isEmpty()) return pool.remove(0);
        throw new RuntimeException("No connections available"); // One could also add further connections here
    }

    public synchronized void releaseConnection(Connection conn) {
        pool.add(conn);
    }
}
