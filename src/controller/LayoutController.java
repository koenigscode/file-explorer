package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import model.FileItem;

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
    public TableView<FileItem> tableView;
    @FXML
    public TableColumn<String, ImageView> tcolType;
    @FXML
    public TableColumn<String, String> tcolName;
    @FXML
    public TableColumn<String, String> tcolSize;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initMenu();
        initTableColumns();
    }

    /**
     * Initialize the TableView's columns
     */
    private void initTableColumns() {
        tcolType.setCellValueFactory(new PropertyValueFactory<>("type"));
        tcolName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcolSize.setCellValueFactory(new PropertyValueFactory<>("size"));
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
