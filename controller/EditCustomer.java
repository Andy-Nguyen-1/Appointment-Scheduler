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
import model.Customer;
import util.DBQuery;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Controller for the editCustomer scene
 */
public class EditCustomer implements Initializable {

    public TextField editCustNameTextField;
    public TextField editCustAddressTextField;
    public TextField editCustPostalCodeTextField;
    public TextField editCustPhoneNumberTextField;
    public ComboBox editCustCountryComboBox;
    public ComboBox editCustFirstLevelDivisionComboBox;
    public Button updateCustomerBtn;
    public Button cancelBtn;
    public TextField updateCustID;
    public Label messageLabel;
    public int divisionId;

    /**
     * Initializes the editCustomer scene and loads data into the editCustCountryComboBox
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Loads customer data from selected customer data
        Customer selectedCustomer = Customers.getSelectedCustomer();
        updateCustID.setText(String.valueOf(selectedCustomer.getCustId()));
        editCustNameTextField.setText(String.valueOf(selectedCustomer.getCustName()));
        editCustAddressTextField.setText(String.valueOf(selectedCustomer.getCustAddress()));
        editCustPostalCodeTextField.setText(String.valueOf(selectedCustomer.getCustPostCode()));
        editCustCountryComboBox.setValue(selectedCustomer.getCustCountry());
        editCustFirstLevelDivisionComboBox.setValue(selectedCustomer.getCustDiv());
        editCustPhoneNumberTextField.setText(String.valueOf(selectedCustomer.getCustPhone()));

        // Load data into the combo boxes
        editCustCountryComboBox.setItems(listCountries());
        editCustFirstLevelDivisionComboBox.setItems(listDivisions());
    }

    /**
     * Updates Division list when a new country is selected.
     * @param actionEvent
     */
    public void updateCountryList(ActionEvent actionEvent) {
        editCustCountryComboBox.getSelectionModel().getSelectedItem();
        updateDivision(actionEvent);
    }

    /**
     * Division combo box, dependent on Country selection.
     * @param actionEvent
     */
    public void updateDivision(ActionEvent actionEvent) {
        editCustFirstLevelDivisionComboBox.setItems(listDivisions());
    }

    /**
     * Save customer function. Check to see if values are null and updates database
     * @param actionEvent
     * @throws SQLException
     * @throws IOException
     */
    public void saveCustomer(ActionEvent actionEvent) throws SQLException, IOException {
        // Clears the messageLabel
        messageLabel.setText("");

        //  Retrieves user input
        String custId = updateCustID.getText();
        String name = editCustNameTextField.getText();
        String address = editCustAddressTextField.getText();
        String postCode = editCustPostalCodeTextField.getText();
        String phone = editCustPhoneNumberTextField.getText();
        String country = editCustCountryComboBox.getValue().toString();
        String division = editCustFirstLevelDivisionComboBox.getValue().toString();

        // Checks if fields are null
        if (name.isEmpty() || address.isEmpty() || postCode.isEmpty() || phone.isEmpty() || country.isEmpty() || division.isEmpty()) {
            messageLabel.setText("Please complete all fields.");
        }

        // Updates the database
        else {
            // Retrieves the division ID for the selected division
            DBQuery.sendQuery("SELECT Division_ID FROM first_level_divisions WHERE Division = \"" + division + "\";");
            ResultSet divResultSet = DBQuery.getQueryResult();
            while (divResultSet.next()) {
                try {
                    this.divisionId = divResultSet.getInt("Division_ID");
                }
                catch (SQLException e) {
                    System.out.println(e.getMessage());
                }

                // Sends the update query.
                DBQuery.sendUpdate("UPDATE customers " +
                        "SET Customer_Name = \"" + name + "\", " +
                        "Address = \"" + address + "\", " +
                        "Postal_Code = \"" + postCode + "\", " +
                        "Phone = \"" + phone + "\", " +
                        "Division_ID = \"" + divisionId + "\"" +
                        "WHERE Customer_ID = " + custId + ";");

                // Informs user that the update was successful and returns to the Customer scene.
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Customer updated!");
                alert.setContentText("Customer has been successfully updated!");
                alert.showAndWait();
                viewCustomer(actionEvent);
            }
        }
    }

    /**
     * Function to load data into the Country combo box.
     * @return countries
     */
    public ObservableList listCountries() {
        ObservableList<String> countries = FXCollections.observableArrayList();

        try {
            // Clears list to prevent duplication.
            countries.removeAll(countries);

            // Retrieves the country names from the database
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
            // Clears the list to prevent duplication.
            divisions.removeAll(divisions);

            // Gets the country ID for the selected country.
            String selectedCountry = editCustCountryComboBox.getSelectionModel().getSelectedItem().toString();
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
     * Function to return the user back to the Customer Records screen.
     * @param actionEvent
     * @throws IOException
     */
    public void viewCustomer(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/customers.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Customer Records");
        stage.setScene(scene);
        stage.show();
    }
}
