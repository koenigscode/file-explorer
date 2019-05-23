package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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

/**
 * @author Koenig Michael
 */
public class LayoutController implements Initializable {
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu menuFile;
    @FXML
    private MenuItem menuItemClose;
    @FXML
    private Menu menuView;
    @FXML
    private CheckMenuItem menuItemShowHidden;
    @FXML
    private Menu menuHelp;
    @FXML
    private MenuItem menuItemAbout;
    @FXML
    private TextField tfFilter;
    @FXML
    private TextField tfPath;
    @FXML
    private TableView<FileItem> tableView;
    @FXML
    private TableColumn<String, ImageView> tcolType;
    @FXML
    private TableColumn<String, String> tcolName;
    @FXML
    private TableColumn<String, String> tcolSize;
    @FXML
    private ImageView imgBack;

    private final ObservableList<FileItem> items = FXCollections.observableArrayList();
    private final FilteredList<FileItem> filteredItems = new FilteredList<>(items, p -> true);
    private final SortedList<FileItem> sortedItems = new SortedList<>(filteredItems);
    private File currentFile;


    private static boolean isJunction(Path p) {
        boolean isJunction = false;
        try {
            isJunction = (p.compareTo(p.toRealPath()) != 0);
        } catch (IOException e) {
            e.printStackTrace();
            e.printStackTrace();
            System.out.println("Could not evalute Path " + p);
        }
        return isJunction;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initMenu();
        initTableView();
        initImgBack();

        Path userHome = Paths.get(System.getProperty("user.home"));
        navigate(userHome.toFile());

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

        tfFilter.textProperty().addListener((observable, oldValue, newValue) -> filter());
        menuItemShowHidden.selectedProperty().addListener((observable, oldValue, newValue) -> filter());
    }

    private void filter() {
        filteredItems.setPredicate(fileItem -> {
            String text = tfFilter.getText();
            if (!menuItemShowHidden.isSelected() && fileItem.getName().charAt(0) == '.') return false;
            if (text == null || text.isEmpty()) return true;
            if (fileItem.getFile().isDirectory()) return false;

            String fileExt = "";
            String filterExt = text;

            int i = fileItem.getName().lastIndexOf('.');
            if (i > 0)
                fileExt = fileItem.getName().substring(i + 1).toLowerCase();


            if (text.charAt(0) == '.')
                filterExt = text.substring(1);

            return fileExt.equals(filterExt.toLowerCase());
        });
    }

    private void goUp() {
        File parent = currentFile.getParentFile();
        if (parent != null)
            navigate(parent);
    }

    private void navigate(File file) {
        if (file == null || !file.isDirectory()) return;
        if (isJunction(file.toPath())) {
            showAlert(Alert.AlertType.ERROR, "Can't Open Junction", "Can't Open Junction",
                    "This file is a junction and could therefore not be opened");
            return;
        }

        items.clear();
        for (File f : file.listFiles()) {
            items.add(new FileItem(f));
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

    private void initTableView() {
        sortedItems.comparatorProperty().set(FileItem::compareTo);
        tableView.setItems(sortedItems);

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

        initTableColumns();
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
