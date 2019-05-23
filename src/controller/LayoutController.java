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
    // wrap FilteredList around items to preserve items list
    private final FilteredList<FileItem> filteredItems = new FilteredList<>(items, p -> true);
    private final SortedList<FileItem> sortedItems = new SortedList<>(filteredItems);
    private File currentFile;

    /**
     * Check if a path is a junction
     *
     * @param p the path to check
     * @return true if the path is a junction, else false
     */
    private static boolean isJunction(Path p) {
        boolean isJunction = false;
        try {
            isJunction = (p.compareTo(p.toRealPath()) != 0);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not evaluate Path " + p);
        }
        return isJunction;
    }

    /**
     * Initialize the controller
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  null if the location is not known.
     * @param resources The resources used to localize the root object, or null if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initMenu();
        initTableView();
        initImgBack();
        initTfPath();
        // listen for extension filters
        tfFilter.textProperty().addListener((observable, oldValue, newValue) -> filter());

        File userHome = Paths.get(System.getProperty("user.home")).toFile();
        navigate(userHome); // navigate to user home on startup

    }

    /**
     * Initialize the path text field
     */
    private void initTfPath() {
        tfPath.setOnAction(event -> {
            try {
                //append separator to end of path as "D:" points to the project directory and "D:\" to the D: drive
                Path path = Paths.get(tfPath.getText() + File.separator);
                if (!path.toFile().exists())
                    showAlert(Alert.AlertType.ERROR, "Path does not exist", "Path does not exist",
                            "The entered path does not exist");
                else
                    navigate(path.toFile());

            } catch (InvalidPathException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Path", "Invalid Path",
                        "You entered an invalid path");
            }
        });
    }

    /**
     * Filter the filteredItems list
     * - filter for extension if entered
     * - filter for hidden files if checkbox is ticked in menubar
     */
    private void filter() {
        filteredItems.setPredicate(fileItem -> {
            String fileExt = "";
            String filterExt = tfFilter.getText();
            int i = fileItem.getFile().getName().lastIndexOf('.');

            if (!menuItemShowHidden.isSelected() && fileItem.getFile().getName().charAt(0) == '.')
                return false;
            if(filterExt.isEmpty() ) return true;
            if(fileItem.getFile().isDirectory()) return false;

            fileExt = fileItem.getFile().getName().substring(i + 1).toLowerCase();
            if (filterExt.charAt(0) == '.')
                filterExt = filterExt.substring(1);

            return fileExt.contains(filterExt.toLowerCase());
        });
    }

    /**
     * Navigate to parent directory
     */
    private void goUp() {
        File parent = currentFile.getParentFile();
        if (parent != null) //check if folder has parent folder
            navigate(parent);
    }

    /**
     * Navigate to the given directory
     *
     * @param file directory to navigate into
     */
    private void navigate(File file) {
        if (file == null || !file.isDirectory()) return;
        if (isJunction(file.toPath())) {
            showAlert(Alert.AlertType.ERROR, "Can't Open Junction", "Can't Open Junction",
                    "This file is a junction and could therefore not be opened");
            return;
        }

        File[] files = file.listFiles();
        if (files == null) return;

        items.clear();
        for (File f : files) {
            items.add(new FileItem(f));
        }

        currentFile = file;
        tfPath.setText(file.getPath());
    }

    /**
     * Open a given file
     *
     * @param file the file to open
     */
    private void open(File file) {
        try {
            Desktop.getDesktop().open(file); // open file in corresponding application
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error opening File", "Could not open file",
                    "An error occurred when trying to open the file in its corresponding program");
        }
    }

    /**
     * Create and show an Alert
     *
     * @param type    the alert's type
     * @param title   the alert's title
     * @param header  the alert's header
     * @param content the alert's text to show
     */
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
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
        menuItemClose.setOnAction(event -> Platform.exit());
        menuItemAbout.setOnAction(event -> {
            String url = "https://github.com/koenigscode/file-explorer";
            try {
                Desktop.getDesktop().browse(java.net.URI.create(url));
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "About page could not be opened", "About page could not be opened", "");
            }
        });
        menuItemShowHidden.selectedProperty().addListener((observable, oldValue, newValue) -> filter());
    }

    /**
     * Initialize the table view
     * - Set up sortedItems list to sort FileItems
     * - Open file or directory on double click
     */
    private void initTableView() {
        sortedItems.comparatorProperty().set(FileItem::compareTo);
        tableView.setItems(sortedItems); // use sortedItems list (= sorted + filtered)

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // mouse button clicked twice
                int index = tableView.getSelectionModel().getSelectedIndex();
                FileItem fi = tableView.getItems().get(index); //clicked FileItem

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
     * - Set up CellFactories
     */
    private void initTableColumns() {
        tcolType.setCellValueFactory(new PropertyValueFactory<>("typeIcon"));
        tcolName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcolSize.setCellValueFactory(new PropertyValueFactory<>("size"));
    }

    /**
     * Initialize back button
     */
    private void initImgBack() {
        imgBack.setOnMouseClicked(event -> goUp());
        // set hand cursor on hover
        imgBack.setOnMouseEntered(event -> imgBack.getScene().setCursor(Cursor.HAND));
        imgBack.setOnMouseExited(event -> imgBack.getScene().setCursor(Cursor.DEFAULT));
    }
}
