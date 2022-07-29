package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointment;
import util.DBQuery;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Initializes the Welcome scene.
 */
public class welcome implements Initializable {

    public Label welcomeLabel;
    public Label viewAppointmentsByLabel;
    public RadioButton allRadioButton;
    public ToggleGroup durationToggleGroup;
    public RadioButton monthRadioButton;
    public RadioButton weekRadioButton;
    public TableView appointmentTable;
    public TableColumn apptIDCol;
    public TableColumn apptTitleCol;
    public TableColumn apptDescCol;
    public TableColumn apptLocationCol;
    public TableColumn apptTypeCol;
    public TableColumn apptStartCol;
    public TableColumn apptEndCol;
    public TableColumn apptCustID;
    public Button addAppointmentBtn;
    public Button editAppointmentBtn;
    public Button deleteAppointmentBtn;
    public Button viewCustomersBtn;
    public Button generateReport;
    public Label apptMessageLabel;


    private static ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    private static Appointment selectedAppointment;
    private static Boolean showAll;
    private static Boolean showWeekly;
    private static Boolean showMonthly;
    public TableColumn apptUserIDCol;
    public TableColumn apptContactCol;

    /**
     * Initiate appointment scene and loads data into the appointments table
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        allRadioButton.setToggleGroup(durationToggleGroup);
        weekRadioButton.setToggleGroup(durationToggleGroup);
        monthRadioButton.setToggleGroup(durationToggleGroup);

        allRadioButton.setSelected(true);
        showAll = true;
        showMonthly = false;
        showWeekly = false;

        apptMessageLabel.setText("");

        try {
            allAppointments();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * Shows all appointments when selected
     * @param actionEvent
     * @throws SQLException
     */
    public void allAppt(ActionEvent actionEvent) throws SQLException {
        showAll = true;
        showMonthly = false;
        showWeekly = false;
        apptMessageLabel.setText("");
        allAppointments();
    }

    /**
     * Shows monthly appointments when selected
     * @param actionEvent
     * @throws SQLException
     */
    public void monthlyAppt(ActionEvent actionEvent) throws SQLException {
        showAll = false;
        showMonthly = true;
        showWeekly = false;
        apptMessageLabel.setText("");
        appointmentByMonth();
    }

    /**
     *  Shows Weekly appointments when selected
     * @param actionEvent
     * @throws SQLException
     */
    public void weeklyAppt(ActionEvent actionEvent) throws SQLException {
        showAll = false;
        showMonthly = false;
        showWeekly = true;
        apptMessageLabel.setText("");
        appointmentByWeek();
    }

    /**
     * Opens add appointment scene
     * @param actionEvent
     * @throws IOException
     */

    public void addAppointment(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/addAppointment.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Add Appointment");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Opens edit appointment scene
     * @param actionEvent
     * @throws IOException
     */

    public void editAppointment(ActionEvent actionEvent) throws IOException {
        selectedAppointment = (Appointment) appointmentTable.getSelectionModel().getSelectedItem();
        apptMessageLabel.setText("");

        // Checks if an appointment is selected
        if (selectedAppointment == null) {
            apptMessageLabel.setText("Please select an appointment to update");
        }

        // Opens the Update Appointment window.
        else {
            Parent root = FXMLLoader.load(getClass().getResource("/view/editAppointment.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("Edit Appointment");
            stage.setScene(scene);
            stage.show();
        }
    }

    /**
     * Removes the selected appointment by appointment ID.
     * Lambda expression is used to run a query to remove the appointment from the database and shows a confirmation message.
     * This allowed for simplified code for a set of results when a user decides to delete an appointment.
     *
     * @param actionEvent
     * @throws SQLException
     */
    public void deleteAppointment(ActionEvent actionEvent) throws SQLException {
        selectedAppointment = (Appointment) appointmentTable.getSelectionModel().getSelectedItem();
        apptMessageLabel.setText("");

        // Checks if an appointment is selected, if not, displays a message.
        if (selectedAppointment == null) {
            apptMessageLabel.setText("Please select an appointment!");
        } else {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Cancellation");
            alert.setHeaderText("Delete Appointment");
            alert.setContentText("Are you sure you want to delete this appointment?");

            // Lambda Expression
            alert.showAndWait().ifPresent((response -> {
                if (response == ButtonType.OK) {
                    int deletedApptId = selectedAppointment.getApptId();
                    String deletedApptType = selectedAppointment.getType();
                    DBQuery.sendUpdate("DELETE FROM appointments " +
                            "WHERE Appointment_ID = " + deletedApptId + ";");
                    // Refreshes the appointments list based on the selected view
                    try {
                        if (showMonthly) {
                            appointmentByMonth();
                        } else if (showWeekly) {
                            appointmentByWeek();
                        } else {
                            allAppointments();
                        }
                        apptMessageLabel.setText("Cancelled Appointment " + deletedApptId + ": " + deletedApptType);
                    }
                    catch (SQLException e){
                        System.out.println(e.getMessage());
                    }
                } else {
                    // Clears message if user decides not to cancel
                apptMessageLabel.setText("");
                }
            }));
        }
    }

    /**
     * Gets the selected appointment.
     * @return Selected appointment.
     */
    public static Appointment selectedAppointment() {
        return selectedAppointment;
    }

    /** Function to get all appointments and display them in the Appointments table. */
    public void allAppointments() throws SQLException {
        // Clears the list to prevent duplication
        appointmentList.removeAll(appointmentList);

        // Sends a query to the appointments table, gets all appointments, and converts to local time
        DBQuery.sendQuery("SELECT * FROM appointments;");
        ResultSet resultSet = DBQuery.getQueryResult();
        loadAppointments(resultSet);

        // Loads the table with the appointments list.
        appointmentTable.setItems(appointmentList);
        apptIDCol.setCellValueFactory(new PropertyValueFactory<>("apptId"));
        apptTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        apptDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        apptLocationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        apptContactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        apptTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        apptStartCol.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
        apptEndCol.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
        apptCustID.setCellValueFactory(new PropertyValueFactory<>("custId"));
        apptUserIDCol.setCellValueFactory(new PropertyValueFactory<>("userID"));
    }

    /**
     * Function to get appointments for the next week and load them in the appointments table.
     * @throws SQLException
     */
    public void appointmentByWeek() throws SQLException {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime weekOut = localDateTime.plusDays(7);

        // Clears the appointments list to prevent duplication.
        appointmentList.removeAll(appointmentList);

        // Sends a query to the appointments table for all appointments that are one month out.
        DBQuery.sendQuery("SELECT * FROM appointments " +
                "WHERE Start >= \"" + localDateTime + "\" AND End <= \"" + weekOut + "\";");
        ResultSet resultSet = DBQuery.getQueryResult();
        loadAppointments(resultSet);

        // Displays a message if there are no appointments for the current week
        if (appointmentList.isEmpty()) {
            apptMessageLabel.setText("No appointments for this week.");
        }
    }

    /**
     * Function to retrieve appointments for the next month and load them in the appointments table.
     * @throws SQLException
     */
    public void appointmentByMonth() throws SQLException {

        LocalDateTime localDateTime = LocalDateTime.now();
        int localMonth = localDateTime.getMonthValue();
        int localYear = localDateTime.getYear();


        appointmentList.removeAll(appointmentList);

        // Sends a query to the appointments table for all appointments that match the current month and year.
        DBQuery.sendQuery("SELECT * FROM appointments " +
                "WHERE MONTH(Start) = \"" + localMonth + "\" AND YEAR(Start) = \"" + localYear + "\";");
        ResultSet resultSet = DBQuery.getQueryResult();
        loadAppointments(resultSet);

        // Displays a message if there are no appointments this month
        if (appointmentList.isEmpty()) {
            apptMessageLabel.setText("No appointments for this month.");
        }
    }

    // Lambda expression which converts a time string
    // to a LocalDateTime object and then converts
    // it from UTC to the user's local time.
    Appointment.dateTimeLambda convertToZonedTime = (String dateTime) -> {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, dateTimeFormat).atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime;
    };

    /**
     * The loadAppointment function reads the appointments from a query result set,
     * convert to users local time, and loads appointments data into the Appointments table.
     *
     * The lambda expression used in this method converts a time string to a LocalDateTime object,
     * then converts it from UTC to the user's local time.
     * The lambda used, simplified the code for time zone conversions, making it more readable and easier to implement.
     * @param resultSet
     */
    public void loadAppointments(ResultSet resultSet) {
        try {
            // Creates a new Appointment for each table row abd converts datetime fields to local time.
            while (resultSet.next()) {
                int appointmentId = resultSet.getInt("Appointment_ID");
                String title = resultSet.getString("Title");
                String description = resultSet.getString("Description");
                String location = resultSet.getString("Location");
                int contactId = resultSet.getInt("Contact_ID");
                String type = resultSet.getString("Type");
                String startDateTime = String.valueOf(convertToZonedTime.localDateTimeConverter(resultSet.getString("Start")));
                String endDateTime = String.valueOf(convertToZonedTime.localDateTimeConverter(resultSet.getString("End")));
                int customerId = resultSet.getInt("Customer_ID");
                int userId = resultSet.getInt("User_ID");

                // Sends a query to the contacts table, returns contact name for each appointment
                DBQuery.sendQuery("SELECT Contact_Name, Contact_ID " +
                        "FROM contacts " +
                        "WHERE Contact_ID = " + contactId + ";");
                ResultSet contactResult = DBQuery.getQueryResult();
                try {
                    while (contactResult.next()) {
                        String contact = contactResult.getString("Contact_Name");
                        Appointment appointment = new Appointment(appointmentId, title, description, location, contact, type, startDateTime, endDateTime, customerId, userId);
                        appointmentList.add(appointment);
                    }
                }
                catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        appointmentTable.setItems(appointmentList);
    }

    /**
     * Opens the Customer directory scene.
     * @param actionEvent
     * @throws IOException
     */
    public void viewCustomer(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/customers.fxml"));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("View Customers");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Opens the generateReports scene.
     * @param actionEvent
     * @throws IOException
     */
    public void generateReport(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/generateReports.fxml"));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Generate Reports");
        stage.setScene(scene);
        stage.show();
    }
}
