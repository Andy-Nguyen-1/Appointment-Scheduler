package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Customer;
import util.DBCustomers;
import util.DBQuery;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * controller for the Customers scene
 */

public class Customers implements Initializable {

    public TableColumn customerIDCol;
    public TableColumn customerNameCol;
    public TableColumn customerAddressCol;
    public TableColumn customerPostalCodeCol;
    public TableColumn customerPhoneCol;
    public TableColumn customerDivisionIDCol;
    public Button addCustomerBtn;
    public Button updateCustomerBtn;
    public Button deleteCustomerBtn;
    public Button backBtn;
    public TableView customerTable;
    public Label messageLabel;
    private static Customer selectedCustomer;
    public TableColumn customerCountryCol;


    /**
     * Initializes the Customer scene and loads data into the table.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            showCustomer();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Opens the Add Customer scene.
     * @param actionEvent
     * @throws IOException
     */
    public void newCustomer(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/addCustomer.fxml"));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Add a new Customer");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Opens the editCustomer scene with data from the selected customer.
     * @param actionEvent
     * @throws IOException
     */
    public void updateCustomer(ActionEvent actionEvent) throws IOException {
        selectedCustomer = (Customer) customerTable.getSelectionModel().getSelectedItem();

        // Checks if a customer is selected.
        if (selectedCustomer == null) {
            messageLabel.setText("Customer not selected!");
        }

        // Opens the editCustomer scene.
        else {
            Parent root = FXMLLoader.load(getClass().getResource("/view/editCustomer.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("Edit Customer");
            stage.setScene(scene);
            stage.show();
        }
    }

    /**
     * Getter to return the selected customer.
     * @return selected customer
     */
    public static Customer getSelectedCustomer() {
            return selectedCustomer;
        }

    /**
     * Deletes the selected customer.
     * @param actionEvent
     * @throws SQLException
     */
    public void deleteCustomer(ActionEvent actionEvent) throws SQLException {
            selectedCustomer = (Customer) customerTable.getSelectionModel().getSelectedItem();

            // Checks if a customer is selected.
            if (selectedCustomer == null) {
                messageLabel.setText("Please select a customer!");
            }

            // Checks if selected customer has an open appointment is customer has an appointment this will prevent deleting.
            else if (customerHasAppointments(selectedCustomer)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Deletion Attempt");
                alert.setHeaderText("Cannot Delete Customer");
                alert.setContentText("This customer has an open appointment and cannot be deleted.");
                alert.showAndWait();
            }

            else {
                // Confirm delete
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Delete");
                alert.setHeaderText("Delete customer?");
                alert.setContentText("Are you sure you want to delete this customer?");
                Optional<ButtonType> result = alert.showAndWait();

                // Deletes the customer from the database and updates the customer table.
                if (result.get() == ButtonType.OK){
                    int deleteCustomerId = selectedCustomer.getCustId();
                    DBQuery.sendUpdate("DELETE FROM customers " +
                            "WHERE Customer_ID = \"" + deleteCustomerId + "\";");
                    showCustomer();
                    messageLabel.setText( deleteCustomerId + " Has been deleted.");
                }
                else {
                    // Clears message
                    messageLabel.setText("");
                }
            }
        }

    /**
     * Function to check if the selected customer has open appointments.
     * @param selectedCustomer
     * @return true or false
     * @throws SQLException
     */
        public boolean customerHasAppointments(Customer selectedCustomer) throws SQLException {
            int selectedCustomerId = selectedCustomer.getCustId();
            boolean hasAppt = false;
            DBQuery.sendQuery("SELECT Customer_ID FROM appointments;");
            ResultSet resultSet = DBQuery.getQueryResult();
            while (resultSet.next()) {
                int appointmentCustId = resultSet.getInt("Customer_ID");
                if (selectedCustomerId == appointmentCustId) {
                    hasAppt = true;
                    break;
                }
                else { hasAppt = false; }
            }
            return hasAppt;
        }

    /**
     * Function to retrieve all customer records and display them in the Customer table.
     * @throws SQLException
     */
    public void showCustomer() throws SQLException {
            customerTable.setItems(DBCustomers.getCustomerList());
            customerIDCol.setCellValueFactory(new PropertyValueFactory<>("custId"));
            customerNameCol.setCellValueFactory(new PropertyValueFactory<>("custName"));
            customerAddressCol.setCellValueFactory(new PropertyValueFactory<>("custAddress"));
            customerPostalCodeCol.setCellValueFactory(new PropertyValueFactory<>("custPostCode"));
            customerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("custPhone"));
            customerDivisionIDCol.setCellValueFactory(new PropertyValueFactory<>("custDiv"));
            customerCountryCol.setCellValueFactory(new PropertyValueFactory<>("custCountry"));
        }

    /**
     * Return user to Welcome scene
     * @param actionEvent
     * @throws IOException
     */
    public void back(ActionEvent actionEvent) throws IOException {
            Parent root = FXMLLoader.load(getClass().getResource("/view/welcome.fxml"));
            Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("Appointments");
            stage.setScene(scene);
            stage.show();
        }
}
