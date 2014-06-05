package gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ChomskyTableController {

    @FXML
    private TableView<CFGFormController.Production> cfgIn;

    @FXML
    private TableColumn<CFGFormController.Production, String> var;
    @FXML
    private TableColumn<CFGFormController.Production, String> go;
    @FXML
    private TableColumn<CFGFormController.Production, String> rule;



}
