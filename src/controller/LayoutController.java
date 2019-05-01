package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LayoutController implements Initializable {
    @FXML
    public MenuBar menuBar;
    @FXML
    public Menu menuFile;
    @FXML
    public MenuItem menuItemClose;
    @FXML
    public Menu MenuHelp;
    @FXML
    public MenuItem menuItemAbout;
    @FXML
    public TextField tfFilter;
    @FXML
    public TableView tableView;
    @FXML
    public TableColumn tcolType;
    @FXML
    public TableColumn tcolName;
    @FXML
    public TableColumn tcolSize;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initMenu();
    }

    /**
     * Initialize the menubar
     */
    private void initMenu() {
        this.menuItemClose.setOnAction(event -> Platform.exit());

        this.menuItemAbout.setOnAction(event -> {
            String url = "https://github.com/koenigscode/file-explorer";

            try {
                java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
            } catch (IOException e) {
                System.out.println("About page could not be opened");
            }
        });
    }
}
