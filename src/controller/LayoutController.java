package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import model.FileItem;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.InvalidPathException;
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
    public TextField tfPath;
    @FXML
    public TableView<FileItem> tableView;
    @FXML
    public TableColumn<String, ImageView> tcolType;
    @FXML
    public TableColumn<String, String> tcolName;
    @FXML
    public TableColumn<String, String> tcolSize;
    @FXML
    private ImageView imgBack;

    private File currentFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initMenu();
        initTableColumns();
        initImgBack();

        Path userHome = Paths.get(System.getProperty("user.home"));
        navigate(userHome.toFile());

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                int index = tableView.getSelectionModel().getSelectedIndex();
                FileItem fi = tableView.getItems().get(index);

                if (fi.getFile().isDirectory())
                    navigate(fi.getFile());
                else
                    open(fi.getFile());
            }
        });

        tfPath.setOnAction(event -> {
            try {
                Path path = Paths.get(tfPath.getText() + "\\");
                if (!path.toFile().exists())
                    showAlert(Alert.AlertType.ERROR, "Path does not exist", "Path does not exist",
                            "The entered path does not exist");
                else
                    navigate(path.toFile());

            } catch (InvalidPathException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Path", "Invalid Path", "You entered an invalid path");
            }
        });

    }

    private void goUp() {
        File parent = currentFile.getParentFile();
        if (parent != null)
            navigate(parent);
    }

    private void navigate(File file) {
        tableView.getItems().clear();
        System.out.println(file.getPath());
        for (File f : file.listFiles()) {
            tableView.getItems().add(new FileItem(f));
        }
        currentFile = file;
        tfPath.setText(file.getPath());
    }

    private void open(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error opening File", "Could not open file",
                    "An error occured when trying to open the file in its corresponding program");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        /*TODO: set parent window*/
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(tableView.getScene().getWindow());
        alert.showAndWait();
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
                System.out.println();
                showAlert(Alert.AlertType.ERROR, "About page could not be opened", "About page could not be opened", "");
            }
        });
    }

    /**
     * Initialize the TableView's columns
     */
    private void initTableColumns() {
        tcolType.setCellValueFactory(new PropertyValueFactory<>("typeIcon"));
        tcolName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcolSize.setCellValueFactory(new PropertyValueFactory<>("size"));
    }

    private void initImgBack() {
        imgBack.setOnMouseClicked(event -> goUp());
        imgBack.setOnMouseEntered(event -> {
            imgBack.getScene().setCursor(Cursor.HAND);
        });
        imgBack.setOnMouseExited(event -> {
            imgBack.getScene().setCursor(Cursor.DEFAULT);
        });
    }
}
