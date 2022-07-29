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
import javafx.stage.Stage;
import util.DBQuery;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * controller for the addCustomer scene
 */
public class AddCustomer implements Initializable {

    public TextField addCustNameTextField;
    public TextField addCustAddressTextField;
    public TextField addCustPostalCodeTextField;
    public TextField addCustPhoneNumberTextField;
    public ComboBox addCustCountryComboBox;
    public ComboBox addCustFirstLevelDivisionComboBox;
    public Button addCustomerBtn;
    public Button cancelBtn;
    public TextField addCustID;
    public Label messageLabel;
    int divisionId;

    /**
     * Initializes the addCustomer scene and loads data into the Country combo box.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addCustCountryComboBox.setItems(listCountries());
    }

    /**
     * Updates State/Territory when a new country is selected.
     * @param actionEvent
     */
        public void addCountryCombo(ActionEvent actionEvent) {
            addCustCountryComboBox.getSelectionModel().getSelectedItem();
            addDivisionCombo(actionEvent);

        }

    /**
     * Handles the Division combo box, dependent on Country selection.
     * @param actionEvent
     */
    public void addDivisionCombo(ActionEvent actionEvent) {
        addCustFirstLevelDivisionComboBox.setItems(listDivisions());
    }

    /**
     * Save new Customer function. Takes user input, check is values are not null and adds the customer to the database
     * @param actionEvent
     * @throws SQLException
     * @throws IOException
     */
    public void saveNewCustomer(ActionEvent actionEvent) throws SQLException, IOException {
        // Clears the messageLabel
        messageLabel.setText("");

        // Takes user input
        String name = addCustNameTextField.getText();
        String address = addCustAddressTextField.getText();
        String postCode = addCustPostalCodeTextField.getText();
        String phone = addCustPhoneNumberTextField.getText();
        String country = (String) addCustCountryComboBox.getValue();
        String division = (String) addCustFirstLevelDivisionComboBox.getValue();

        // Check to make sure no fields are null.
        if (name.isEmpty() || address.isEmpty() || postCode.isEmpty() || phone.isEmpty() || country == null  || division == null) {
            messageLabel.setText("Please complete all required fields.");
        }

        // Updates the database.
        else {
            // Gets the division ID for the selected division
            DBQuery.sendQuery("SELECT Division_ID FROM first_level_divisions WHERE Division = \"" + division + "\";");
            ResultSet divResultSet = DBQuery.getQueryResult();
            while (divResultSet.next()) {
                try {
                    this.divisionId = divResultSet.getInt("Division_ID");
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }

            // Sends the query to insert the new customer
            DBQuery.sendUpdate("INSERT INTO customers " +
                    "(Customer_Name, Address, Postal_Code, Phone, Division_ID)" +
                    "VALUES ( \"" + name + "\", \"" + address + "\", \"" + postCode + "\", \"" + phone + "\", " + divisionId + ");");

            // Update successful message after customer has been added to the database
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Success!");
            alert.setContentText("The new customer record successfully added to database.");
            alert.showAndWait();
            back(actionEvent);
        }
    }

    /**
     * Function to load data into the Country combo box.
     * @return Country
     */
    public ObservableList listCountries() {
        ObservableList<String> countries = FXCollections.observableArrayList();

        try {
            // Clears list to prevent duplication.
            countries.removeAll(countries);

            // Gets country names from the database
            DBQuery.sendQuery("SELECT Country FROM countries");
            ResultSet resultSet = DBQuery.getQueryResult();
            while (resultSet.next()) {
                countries.add(resultSet.getString("Country"));
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return countries;
    }

    /**
     * Function to load data into the Division combo box based on the selected country.
     * @return divisions
     */
    public ObservableList listDivisions() {
        ObservableList<String> divisions = FXCollections.observableArrayList();

        try {
            // Clears list to prevent duplication.
            divisions.removeAll(divisions);

            // Gets the country ID for the selected country.
            String selectedCountry = addCustCountryComboBox.getSelectionModel().getSelectedItem().toString();
            DBQuery.sendQuery("SELECT Country_ID FROM countries WHERE Country = \"" + selectedCountry + "\";");
            ResultSet countryResultSet = DBQuery.getQueryResult();
            while (countryResultSet.next()) {
                try {
                    int countryId = countryResultSet.getInt("Country_ID");

                    // Gets divisions that match the country ID for the selected country
                    DBQuery.sendQuery("SELECT Division FROM first_level_divisions WHERE COUNTRY_ID = \"" + countryId + "\";");
                    ResultSet divResultSet = DBQuery.getQueryResult();
                    while (divResultSet.next()) {
                        divisions.add(divResultSet.getString("Division"));
                    }
                }
                catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return divisions;
    }

    /**
     * Function to return the user back to the Customer scene.
     * @param actionEvent
     * @throws IOException
     */
    public void back(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/customers.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Customer Records");
        stage.setScene(scene);
        stage.show();
    }
}
