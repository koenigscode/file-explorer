package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import model.FileItem;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class LayoutController implements Initializable {
    @FXML
    public MenuBar menuBar;
    @FXML
    public Menu menuFile;
    @FXML
    public MenuItem menuItemClose;
    @FXML
    public Menu menuHelp;
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

        Path userHome = Paths.get(System.getProperty("user.home"));
        navigate(userHome.toFile());

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                int index = tableView.getSelectionModel().getSelectedIndex();
                FileItem fi = tableView.getItems().get(index);
                System.out.println(fi.getFile().isDirectory());

                if (fi.getFile().isDirectory())
                    navigate(fi.getFile());
                else
                    open(fi.getFile());
            }

        });

    }

    private void navigate(File file) {
        tableView.getItems().clear();
        for (File f : file.listFiles()) {
            tableView.getItems().add(new FileItem(f));
        }
    }

    private void open(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error opening File");
            alert.setHeaderText("Could not open file");
            alert.setContentText("An error occured when trying to open the file in its corresponding program");
            alert.showAndWait();
        }
    }

    /**
     * Initialize the TableView's columns
     */
    private void initTableColumns() {
        tcolType.setCellValueFactory(new PropertyValueFactory<>("typeIcon"));
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
