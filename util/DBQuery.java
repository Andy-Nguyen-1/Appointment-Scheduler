package util;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Util class to send queries to the database and retrieve results.
 */
public class DBQuery {
    private static ResultSet resultSet;

    /**
     * Creates a statement object to query the database.
     * @param query
     */
    public static void sendQuery(String query) {
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            resultSet = statement.executeQuery(query);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     *
     * @return results of a query
     */
    public static ResultSet getQueryResult() {
        return resultSet;
    }

    /**
     * Creates a statement object to update the database.
     * @param query
     */
    public static void sendUpdate(String query) {
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            statement.executeUpdate(query);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
