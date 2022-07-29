package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Util class to connect the database
 */
public class DBConnection {
    //JDBC URL parts
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String ipAddress = "//localhost/";
    private static final String dbName = "client_schedule";

    //JDBC URL parts
    private static final String jdbcUrl = protocol + vendor + ipAddress + dbName;
    //Driver and connection interface reference
    private static final String mySqlJdbcDriver = "com.mysql.cj.jdbc.Driver";
    private static Connection conn = null;

    //JDBC driver username and password
    private static final String username = "root";
    private static final String password = "Passw0rd!";

    public static Connection startConnection() {
        try{
            Class.forName(mySqlJdbcDriver);
            conn = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("Connection Successful");
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * Connect to database
     * @return conn
     */
    public static Connection getConnection() {
        return conn;
    }

    /**
     * Close database connection
     */
    public static void closeConnection(){
        try {
            conn.close();
        }
        catch (Exception e) {
            // Do nothing if connection is already closed.
        }
        System.out.println("Disconnected from database.");
    }

}
