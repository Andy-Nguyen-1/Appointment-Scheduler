package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.DBQuery;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * logIn_Form Controller.
 */
public class logIn_Form implements Initializable {

    public TextField usernameTextField;
    public Label usernameLabel;
    public Label passwordLabel;


    public TextField passwordTextField;

    public Label currLocation;
    public Button exitBtn;
    public Label userTimeZoneLabel;
    public Label currTimeZone;
    public Label userLocationLabel;
    public Label welcomeMessageLabel;
    public Label signInLabel;
    public Button logInBtn;
    public Label logInError;


    /** Initializes logIn scene and displays timezone and language.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Displays the users timezone
        try {
            // Displays user timezone, location and language ID.
            TimeZone timeZone = TimeZone.getDefault();
            currTimeZone.setText(timeZone.getID());
            currLocation.setText(String.valueOf(Locale.getDefault()));

            // Determines the default language for the login screen.
            if (Locale.getDefault().getLanguage().equals("fr")) {
                Locale.setDefault(new Locale("fr"));
            }
            // Retrieves login screen text from the lang resource bundle.
            ResourceBundle rb = ResourceBundle.getBundle("languages/lang", Locale.getDefault());
            welcomeMessageLabel.setText(rb.getString("welcomeMessageTranslate"));
            signInLabel.setText(rb.getString("signInTranslate"));
            usernameLabel.setText(rb.getString("usernameTranslate"));
            passwordLabel.setText(rb.getString("passwordTranslate"));
            userLocationLabel.setText(rb.getString("userLocationTranslate"));
            userTimeZoneLabel.setText(rb.getString("userTimeZoneTranslate"));
            logInBtn.setText(rb.getString("logInTranslate"));
            exitBtn.setText(rb.getString("exitButtonTranslate"));
            logInError.setText(rb.getString("errorMessageTranslate"));

            // Hides login error message.
            logInError.setVisible(false);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void checkLogIn(ActionEvent actionEvent) throws IOException, SQLException {
        // Gets user input
        String usernameIn = usernameTextField.getText();
        String passwordIn = passwordTextField.getText();

        // Checks login
        boolean user = verifyLogin(usernameIn, passwordIn);

        // Open the Appointments screen if the login is successful.
        if (user) {
            // Record successful login to login_activity.txt
            FileWriter fileWriter = new FileWriter("login_activity.txt", true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println("Successful Login: " + usernameIn + " on " + LocalDateTime.now());
            printWriter.close();

            // Notify the user if there is an appointment in 15 minutes
            upcomingAppointment();

            // Open the Welcome screen
            Parent root = FXMLLoader.load(getClass().getResource("/view/welcome.fxml"));
            Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("Welcome!");
            stage.setScene(scene);
            stage.show();
        }
        // Show error message if the username and password are wrong.
        else {
            // Record failed login
            FileWriter fileWriter = new FileWriter("login_activity.txt", true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println("Failed Login: " + usernameIn + " on " + LocalDateTime.now());
            printWriter.close();

            // Show error message
            logInError.setVisible(true);
        }
    }
    private boolean verifyLogin(String username, String password) throws SQLException {
        try {
            // Send a query to the users table
            DBQuery.sendQuery("SELECT * FROM users;");
            ResultSet resultSet = DBQuery.getQueryResult();

            // search users table and check input against stored data.
            while (resultSet.next()) {
                if (resultSet.getString("User_Name").equals(username) &&
                        resultSet.getString("Password").equals(password)) {
                    return true;
                }
                else { return false; }
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     *  Function to check if there is an appointment within 15 minutes from login.
     */
    public void upcomingAppointment() {
        LocalDateTime currentUtcDT = LocalDateTime.now(ZoneId.of("UTC"));
        LocalDateTime timeIn15Minutes = LocalDateTime.now(ZoneId.of("UTC")).plusMinutes(15);

        try {
            // Send query start times in the appointments table
            DBQuery.sendQuery("SELECT * FROM appointments " +
                    "WHERE Start BETWEEN \"" + currentUtcDT + "\" AND \"" + timeIn15Minutes + "\";");
            ResultSet resultSet = DBQuery.getQueryResult();
            resultSet.next();
            LocalDateTime apptStart = resultSet.getTimestamp("Start").toLocalDateTime();

            // Show an alert if there are upcoming appointments
            int apptId = resultSet.getInt("Appointment_ID");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Upcoming Appointment");
            alert.setHeaderText("You have an appointment within the next 15 minutes!");
            alert.setContentText("Appointment " + apptId + " starts at " + apptStart.getHour() + ":" + apptStart.getMinute() + " on " + apptStart.getMonth() + " " + apptStart.getDayOfMonth() + ".");
            alert.showAndWait();
        } catch (SQLException e) {
            // Notify the user if there aren't any upcoming appointments
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Appointments");
            alert.setHeaderText("No Upcoming Appointments");
            alert.setContentText("There are no appointments starting within the next 15 minutes.");
            alert.showAndWait();
        }

    }

    /**
     * Exit the application.
     * @param actionEvent
     */
    public void exit(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

}
