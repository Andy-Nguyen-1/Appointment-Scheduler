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
import util.DBQuery;


import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.util.ResourceBundle;
import java.util.TimeZone;


/**
 *  controller for the add appointment scene that controls the logic to add appointments
 */
public class AddAppointment implements Initializable {
    @FXML
    public TextField appointmentID;
    @FXML
    public TextField apptTitleTextField;
    @FXML
    public TextArea apptDescTextArea;
    @FXML
    public TextField locationTextField;
    @FXML
    public ComboBox contactComboBox;
    @FXML
    public TextField typeTextField;
    @FXML
    public DatePicker startDatePicker;
    @FXML
    public Button addAppointmentBtn;
    @FXML
    public Button cancelBtn;
    @FXML
    public Label messageLabel;
    @FXML
    public ComboBox userIDComboBox;
    public ComboBox apptCustIDCombo;
    public ComboBox startTimeCombo;
    public ComboBox endTimeCombo;
    public Label messagelLabel;

    @FXML
    int contactId;

    /**
     * Initialize the addAppointment scene.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Load data into the Contact combo box
        contactComboBox.setItems(listContacts());

        // Load data into the User ID combo Box
        userIDComboBox.setItems(userIDList());

        // Load data into the User ID combo Box
        apptCustIDCombo.setItems(custIDList());

        // Load data into time combo
        startTimeCombo.setItems(timeSlots());
        endTimeCombo.setItems(timeSlots());

        startDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable( empty || date.isBefore(LocalDate.now()));
                if (date.isBefore(LocalDate.now())) {
                    setStyle("-fx-background-color: #808080;");
                }
            }
        });


    }

    /**
     * Save appointment function, checks to make sure no fields are null, and adds appointment to the database
     * @param actionEvent
     * @throws SQLException
     * @throws IOException
     */
    public void saveAppointment(ActionEvent actionEvent) throws SQLException, IOException {


        // Takes user input
        String apptId = appointmentID.getText();
        String title = apptTitleTextField.getText();
        String description = apptDescTextArea.getText();
        String location = locationTextField.getText();
        String contact = contactComboBox.getValue().toString();
        String type = typeTextField.getText();
        String custId = apptCustIDCombo.getValue().toString();
        LocalDate startDate = startDatePicker.getValue();
        String startTimeString = startTimeCombo.getValue().toString();
        String endTimeString = endTimeCombo.getValue().toString();
        String userID = userIDComboBox.getValue().toString();


            // Converts the start time and end time to LocalTime.
            LocalTime startTime = LocalTime.parse(startTimeString);
            LocalTime endTime = LocalTime.parse(endTimeString);

            // Converts start date/time and end date/time to LocalDateTime.
            LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
            LocalDateTime endDateTime = LocalDateTime.of(startDate, endTime);

            // Checks if there are overlapping appointments.
            if (overlappingAppointments(startDateTime, endDateTime)) {
                messageLabel.setText("Overlapping appointment, already scheduled.");
            } else {
                // Converts LocalDateTime to ZonedDateTime
                ZoneId userZone = ZoneId.of(TimeZone.getDefault().getID());
                ZonedDateTime startZonedDT = ZonedDateTime.of(startDateTime, userZone);
                ZonedDateTime endZonedDT = ZonedDateTime.of(endDateTime, userZone);

                // Calculates the UTC time from the zoned start and end date-times
                ZonedDateTime startUtcDT = startZonedDT.withZoneSameInstant(ZoneId.of("UTC"));
                ZonedDateTime endUtcDT = endZonedDT.withZoneSameInstant(ZoneId.of("UTC"));

                // Prepares input for database and adds appointment to database

                // Converts UTC times to LocalDateTime for database storage
                LocalDateTime startLocalUtc = startUtcDT.toLocalDateTime();
                LocalDateTime endLocalUtc = endUtcDT.toLocalDateTime();

                // Gets the contact ID for the selected contact
                DBQuery.sendQuery("SELECT Contact_ID FROM contacts WHERE Contact_Name = \"" + contact + "\";");
                ResultSet resultSet = DBQuery.getQueryResult();
                while (resultSet.next()) {
                    try {
                        this.contactId = resultSet.getInt("Contact_ID");
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                }

                // Adds the appointment to the database
                DBQuery.sendUpdate("INSERT INTO appointments " +
                        "(Title, Description, Location, Type, Start, End, Customer_ID, Contact_ID, User_ID) " +
                        "VALUES (\"" + title + "\", \"" + description + "\", \"" + location + "\", \"" + type + "\", \"" +
                        startLocalUtc + "\", \"" + endLocalUtc + "\", " + custId + ", " + contactId + "," + userID + ");");

                // Informs user that the update was successful and returns to the Welcome scene.
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Appointment added!");
                alert.setContentText("Appointment was successfully saved to database.");
                alert.showAndWait();
                back(actionEvent);
            }

    }


    public boolean verifyAppt(TextField apptTitleTextField, TextArea apptDescTextArea, TextField locationTextField, ComboBox typeTextField,
                              TextField contactComboBox, ComboBox startDatePicker, DatePicker startTimeCombo, ComboBox endTimeCombo,
                              ComboBox apptCustIDCombo, ComboBox userIDComboBox){

        if (apptTitleTextField == null) {
            messageLabel.setText("Please enter appointment title");
            return true;
        }
        if (apptDescTextArea == null) {
            messageLabel.setText("Please enter appointment description");
            return true;
        }
        if (locationTextField == null) {
            messageLabel.setText("Please enter a location");
            return true;
        }
        if (typeTextField == null){
            messageLabel.setText("Please enter a type");
            return true;
        }
        if (contactComboBox == null) {
            messageLabel.setText("Please select a contact");
            return true;
        }
        if (startDatePicker == null) {
            messageLabel.setText("Please select a start date");
            return true;
        }
        if (startTimeCombo == null) {
            messageLabel.setText("Please select a start time");
            return true;
        }
        if(endTimeCombo == null){
            messageLabel.setText("Please select an end time");
            return true;
        }
        if (apptCustIDCombo == null){
            messageLabel.setText("Please select customer ID");
            return true;
        }
        if (userIDComboBox == null){
            messageLabel.setText("Please select a user ID");
            return true;
        }
        else{
            return false;
        }

    }

    /**
     * Function to load data to the Contact combo box.
     * @return contacts
     */
    public ObservableList listContacts() {
        ObservableList<String> contacts = FXCollections.observableArrayList();

        try {
            // Clear the list to prevent duplication.
            contacts.removeAll(contacts);

            // Get contact names from the database
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
     * Function to load data to the User ID combo box.
     * @return userID
     */
    public ObservableList userIDList() {
        ObservableList<String> userID = FXCollections.observableArrayList();

        try {
            // Clears the list to prevent duplication.
            userID.removeAll(userID);

            // Gets contact names from the database
            DBQuery.sendQuery("SELECT User_ID FROM USERS");
            ResultSet resultSet = DBQuery.getQueryResult();
            while (resultSet.next()) {
                userID.add(resultSet.getString("User_ID"));
            }

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return userID;
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
    public static ObservableList<String> timeSlots () {
        ObservableList<String> times = FXCollections.observableArrayList("08:00","08:15","08:30","08:45","09:00",
                "09:15", "09:30", "09:45", "10:00", "10:15", "10:30", "10:45", "11:00", "11:15", "11:30", "11:45", "12:00",
                "12:15", "12:15", "12:30", "12:45", "13:00", "13:15", "13:30", "13:45", "14:00", "14:15", "14:30", "14:45",
                "15:00", "15:15", "15:30", "15:45", "16:00", "17:15", "17:30", "17:45", "18:00", "18:15", "18:30", "18:45",
                "19:00", "19:15", "19:30", "19:45",
                "20:00", "20:15", "20:30", "20:45",
                "21:00", "21:15", "21:30", "21:45",
                "22:00");

        return times;
    }

    /**
     * Function to check if there are any overlapping appointments
     * @param startNewAppt
     * @param endNewAppt
     * @return true or false
     * @throws SQLException
     */
    public static boolean overlappingAppointments(LocalDateTime startNewAppt, LocalDateTime endNewAppt) throws SQLException {
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
     * Function to send the user back to the Welcome scene.
     * @param actionEvent
     * @throws IOException
     */
    public void back(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/welcome.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Appointments");
        stage.setScene(scene);
        stage.show();
    }

}


