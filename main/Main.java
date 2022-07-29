package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.DBConnection;

/**
 * Main Class. This is where the application launches.
 */
public class Main extends Application {
    /**
     * The start method creates the FXML stage and loads the log in scene.
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/logIn_Form.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Log-in Form");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Main function that launches the application and starts a connection to the database and terminates when
     * application is closed
     * @param args
     */
    public static void main(String[] args){
        DBConnection.startConnection();
        launch(args);
        DBConnection.closeConnection();
    }
}

