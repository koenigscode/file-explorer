package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LayoutController {
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
}
