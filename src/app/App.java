package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Koenig Michael
 */
public class App extends Application {

    /**
     * Main entry point for the JavaFX application
     *
     * @param primaryStage primary stage for the application on which the scene will be set
     * @throws java.lang.Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Layout.fxml"));
        primaryStage.setTitle("File Explorer");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
     * Launch the application
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
