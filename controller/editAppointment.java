package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Appointment;
import util.DBQuery;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;
import java.util.TimeZone;

import static controller.AddAppointment.timeSlots;

/**
 * editAppointment controller
 */
public class editAppointment implements Initializable {
    @FXML
    public TextField updateTitleTextField;
    @FXML
    public TextField updateLocationTextField;
    @FXML
    public ComboBox updateContactComboBox;
    @FXML
    public TextField updateTypeTextField;
    @FXML
    public DatePicker updateStartDatePicker;
    @FXML
    public DatePicker updateEndDatePicker;
    @FXML
    public Button updateAppointmentBtn;
    @FXML
    public Button cancelBtn;
    @FXML
    public Label messageLabel;
    @FXML
    public TextField appointmentID;
    @FXML
    public TextArea updateDescTextArea;

    @FXML
    public TextField updateStartTime;
    @FXML
    public TextField updateEndTime;
    @FXML
    public ComboBox updateUserIDComboBox;
    public ComboBox custIDCombo;
    public ComboBox startTimeCombo;
    public ComboBox endTimeCombo;
    DateTimeFormatter dateTimeFormat = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    int contactId;

    /**
     * Initialize the editAppointment scene.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Clears the messageLabel
        messageLabel.setText("");

        // Load the appointment with selected appointment data
        Appointment selectedAppointment = welcome.selectedAppointment();
        appointmentID.setText(String.valueOf(selectedAppointment.getApptId()));
        updateTitleTextField.setText(String.valueOf(selectedAppointment.getTitle()));
        updateDescTextArea.setText(String.valueOf(selectedAppointment.getDescription()));
        updateContactComboBox.setValue(String.valueOf(selectedAppointment.getContact()));
        updateUserIDComboBox.setValue(String.valueOf(selectedAppointment.getUserID()));
        updateLocationTextField.setText(String.valueOf(selectedAppointment.getLocation()));
        updateTypeTextField.setText(String.valueOf(selectedAppointment.getType()));
        custIDCombo.setValue(String.valueOf(selectedAppointment.getCustId()));



        // Load Customer ID Combo
        custIDCombo.setItems(custIDList());

        // Loads data into the updateContactComboBox
        updateContactComboBox.setItems(listContacts());
        // Loads data into the updateUserIDComboBox
        updateUserIDComboBox.setItems(userIDList());

        startTimeCombo.setItems(timeSlots());
        endTimeCombo.setItems(timeSlots());


        // Set the start and end dates and times
        LocalDateTime startDT = LocalDateTime.parse(selectedAppointment.getStartDateTime(), dateTimeFormat);
        LocalDateTime endDT = LocalDateTime.parse(selectedAppointment.getEndDateTime(), dateTimeFormat);

        LocalDate startDate = startDT.toLocalDate();
        LocalTime startTime = startDT.toLocalTime();
        LocalDate endDate = startDT.toLocalDate();
        LocalTime endTime = endDT.toLocalTime();

        updateStartDatePicker.setValue(startDate);
        startTimeCombo.setValue(String.valueOf(startTime));
        endTimeCombo.setValue(String.valueOf(endTime));
    }

    /**
     * Save changes function, checks if there are any null values, and check to see if time is within business hours.
     *      * Then updates the database.
     * @param actionEvent
     * @throws SQLException
     * @throws IOException
     */
    public void saveAppointment(ActionEvent actionEvent) throws SQLException, IOException {
        // Clears the messageLabel
        messageLabel.setText("");

        // Retrieves user input
        String apptId = appointmentID.getText();
        String title = updateTitleTextField.getText();
        String description = updateDescTextArea.getText();
        String location = updateLocationTextField.getText();
        String contact = updateContactComboBox.getValue().toString();
        String type = updateTypeTextField.getText();
        String custId = custIDCombo.getValue().toString();
        LocalDate startDate = updateStartDatePicker.getValue();
        String startTimeString = startTimeCombo.getValue().toString();
        LocalDate endDate = updateStartDatePicker.getValue();
        String endTimeString = endTimeCombo.getValue().toString();
        String userID = updateUserIDComboBox.getValue().toString();

            // Converts start time and end time to LocalTime.
            LocalTime startTime = LocalTime.parse(startTimeString);
            LocalTime endTime = LocalTime.parse(endTimeString);

            // Converts start date/time and end date/time to LocalDateTime.
            LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
            LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

            // Checks if there are any overlapping appointments.
            if (overlappingAppointments(startDateTime, endDateTime)) {
                messageLabel.setText("Overlapping appointments scheduled. Please pick a different time.");
            } else {

                // Converts LocalDateTime to ZonedDateTime
                ZoneId userZone = ZoneId.of(TimeZone.getDefault().getID());
                ZonedDateTime startZonedDT = ZonedDateTime.of(startDateTime, userZone);
                ZonedDateTime endZonedDT = ZonedDateTime.of(endDateTime, userZone);

                // Calculates the UTC time from the zoned start and end date-times
                ZonedDateTime startUtcDT = startZonedDT.withZoneSameInstant(ZoneId.of("UTC"));
                ZonedDateTime endUtcDT = endZonedDT.withZoneSameInstant(ZoneId.of("UTC"));

                // Checks if the entered times are outside of business hours (22:00 - 08:00 EST).
                ZonedDateTime startEstDT = startUtcDT.withZoneSameInstant(ZoneId.of("America/New_York"));
                ZonedDateTime endEstDT = endUtcDT.withZoneSameInstant(ZoneId.of("America/New_York"));
                int startEstHour = startEstDT.getHour();
                int endEstHour = endEstDT.getHour();
                int endEstMinute = endEstDT.getMinute();

                if (startEstHour < 8 || startEstHour > 22 || endEstHour > 22 || endEstHour < 8) {
                    messageLabel.setText("Please schedule a time between our business hours of 8:00 - 22:00 EST");
                } else if (endEstHour == 22 && endEstMinute > 0) {
                    messageLabel.setText("Please schedule a time between our business hours of 8:00 - 22:00 EST");
                }

                 // Prepares input for database and updates appointment to database
                else {
                    // Converts UTC times to LocalDateTime for database storage
                    LocalDateTime startLocalUtc = startUtcDT.toLocalDateTime();
                    LocalDateTime endLocalUtc = endUtcDT.toLocalDateTime();

                    // Gets the contactID for the selected contact
                    DBQuery.sendQuery("SELECT Contact_ID FROM contacts WHERE Contact_Name = \"" + contact + "\";");
                    ResultSet resultSet = DBQuery.getQueryResult();
                    while (resultSet.next()) {
                        try {
                            this.contactId = resultSet.getInt("Contact_ID");
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }
                    }

                    // Updates the database with the entered values
                    DBQuery.sendUpdate("UPDATE appointments " +
                            "SET Title = \"" + title + "\", " +
                            "Description = \"" + description + "\", " +
                            "Location = \"" + location + "\", " +
                            "Type = \"" + type + "\", " +
                            "Start = \"" + startLocalUtc + "\", " +
                            "End = \"" + endLocalUtc + "\", " +
                            "Customer_ID = " + custId + ", " +
                            "Contact_ID = " + contactId +  ", " +
                            "User_ID = " + userID + " " +
                            "WHERE Appointment_ID = " + apptId + ";");

                    // Informs user that the update was successful and returns to the Welcome scene.
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText("Appointment updated!");
                    alert.setContentText("The appointment has been successfully updated!");
                    alert.showAndWait();
                    back(actionEvent);
                }
            }
        }

    public ObservableList custIDList() {
        ObservableList<String> custID = FXCollections.observableArrayList();

        try {
            // Clears the list to prevent duplication.
            custID.removeAll(custID);

            // Gets contact names from the database
            DBQuery.sendQuery("SELECT Customer_ID FROM customers");
            ResultSet resultSet = DBQuery.getQueryResult();
            while (resultSet.next()) {
                custID.add(resultSet.getString("Customer_ID"));
            }

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return custID;
    }

    public boolean overlappingAppointments(LocalDateTime startNewAppt, LocalDateTime endNewAppt) throws SQLException {
        boolean overlapExists = false;
        DBQuery.sendQuery("SELECT * FROM appointments");
        ResultSet resultSet = DBQuery.getQueryResult();
        while (resultSet.next()) {
            LocalDateTime startExistingAppt = resultSet.getTimestamp("Start").toLocalDateTime();
            LocalDateTime endExistingAppt = resultSet.getTimestamp("End").toLocalDateTime();
            if (startNewAppt.isEqual(startExistingAppt) || startNewAppt.isEqual(endExistingAppt)) {
                overlapExists = true;
                break;
            }
            else if (startNewAppt.isAfter(startExistingAppt) && startNewAppt.isBefore(endExistingAppt)) {
                overlapExists = true;
                break;
            }
            else if (endNewAppt.isEqual(startExistingAppt) || endNewAppt.isEqual(endExistingAppt)) {
                overlapExists = true;
                break;
            }
            else if (endNewAppt.isAfter(startExistingAppt) && endNewAppt.isBefore(endExistingAppt)) {
                overlapExists = true;
                break;
            }
            else {
                overlapExists = false;
            }
        }

        return overlapExists;
    }

    /**
     * Function to load data into the Contact combo box.
     * @return contacts
     */
    public ObservableList listContacts() {
        ObservableList<String> contacts = FXCollections.observableArrayList();

        try {
            // Clears list to prevent duplication.
            contacts.removeAll(contacts);

            // Gets contact names from the database
            DBQuery.sendQuery("SELECT Contact_Name FROM contacts");
            ResultSet resultSet = DBQuery.getQueryResult();
            while (resultSet.next()) {
                contacts.add(resultSet.getString("Contact_Name"));
            }

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return contacts;
    }

    /**
     * Function to load data into the userID combo box.
     * @return userID
     */
    public ObservableList userIDList() {
        ObservableList<Integer> userID = FXCollections.observableArrayList();

        try {
            // Clears the list to prevent duplication.
            userID.removeAll(userID);

            // Gets contact names from the database
            DBQuery.sendQuery("SELECT User_ID FROM USERS");
            ResultSet resultSet = DBQuery.getQueryResult();
            while (resultSet.next()) {
                userID.add(resultSet.getInt("User_ID"));
            }

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return userID;
    }

    /**
     * Function to send the user back to the Appointments screen.
     * @param actionEvent
     * @throws IOException
     */
    public void back(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/welcome.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Welcome!");
        stage.setScene(scene);
        stage.show();
    }
}

