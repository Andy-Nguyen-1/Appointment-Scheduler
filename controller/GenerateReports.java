package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import util.DBQuery;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Controller for the generateReport scene
 * */
public class GenerateReports {
    public TextArea showReport;
    public Label messageLabel;

    /**
     * This function counts the total appointments by month and type
     */
    public void generateReport1(ActionEvent actionEvent) throws SQLException {
        StringBuilder buildReport = new StringBuilder();
        showReport.setText("");

        //Query to get total appointments by type and month
        DBQuery.sendQuery("SELECT MONTH(Start) AS \"Month\", Type, COUNT(Type) AS \"Count\" " + "FROM appointments " +
                "GROUP BY MONTH(Start), Type;");
        ResultSet resultSet = DBQuery.getQueryResult();
        while (resultSet.next()) {
            int month = resultSet.getInt("Month");
            String type = resultSet.getString("Type");
            int count = resultSet.getInt("Count");
            buildReport.append("Month: " + month + " Type: " + type + " Total Appointments: " + count + "\n");

            showReport.setText(buildReport.toString());
        }
    }

    /**
     * This Function generates scheduled appointments for each contact.
     */
    public void generateReport2(ActionEvent actionEvent) throws SQLException {
        StringBuilder buildReport = new StringBuilder();
        showReport.setText("");

        // Query to retrieve appointments and order them by contact ID
        DBQuery.sendQuery("SELECT Appointment_ID, Title, Description, Type, Start, End, Customer_ID, Contact_ID " +
                "FROM appointments " +
                "ORDER BY Contact_ID;");
        ResultSet resultSet = DBQuery.getQueryResult();
        while (resultSet.next()) {
            int contactId = resultSet.getInt("Contact_ID");
            int appointmentId = resultSet.getInt("Appointment_ID");
            String title = resultSet.getString("Title");
            String description = resultSet.getString("Description");
            String type = resultSet.getString("Type");
            String start = resultSet.getString("Start");
            String end = resultSet.getString("End");
            int customerId = resultSet.getInt("Customer_ID");
            buildReport.append("Contact " + contactId + "  Appt ID: " + appointmentId + "  Title: " + title + "  Desc: " + description + "  Type: " + type + "  Start: " + start + "  End: " + end + "  Cust ID: " + customerId + "\n\n ");
        }

        // Shows schedule for each contact
        showReport.setText(buildReport.toString());
    }

    /**
     * This Function retrieves the total number of customers from each country.
     * @param actionEvent
     * @throws SQLException
     */
    public void generateReport3(ActionEvent actionEvent) throws SQLException {
        StringBuilder buildReport = new StringBuilder();
        showReport.setText("");
        DBQuery.sendQuery("SELECT COUNT(c.Customer_ID) AS \"Count\", y.Country " +
                "FROM customers AS c " +
                "JOIN first_level_divisions AS d ON c.Division_ID = d.Division_ID " +
                "JOIN countries AS y ON d.COUNTRY_ID = y.Country_ID " +
                "GROUP BY y.Country;");
        ResultSet resultSet = DBQuery.getQueryResult();
        while (resultSet.next()) {
            int customerCount = resultSet.getInt("Count");
            String country = resultSet.getString("Country");
            buildReport.append(country + " Total Customers: " + customerCount + "\n");
        }

        // Shows the total number of customers from each country
        showReport.setText(buildReport.toString());
    }

    /**
     *  Return to the Welcome scene.
     * @param actionEvent
     * @throws IOException
     */
    public void backToMain(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/welcome.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Welcome!");
        stage.setScene(scene);
        stage.show();
    }
}










