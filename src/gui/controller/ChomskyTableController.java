package gui.controller;

import cfg.CFG;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ChomskyTableController implements Initializable {

    @FXML
    private TableView<Production> cfgIn;

    @FXML
    private TableColumn<Production, String> var;
    @FXML
    private TableColumn<Production, String> go;
    @FXML
    private TableColumn<Production, String> rule;

    private CFG cfg;

    public void setCfg(CFG cfg) {
        this.cfg = cfg;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Production> productions = new ArrayList<>();
        if(cfg != null){
            for(String key : cfg.getProductions().keySet()){
                for(String production : cfg.getProductions().get(key)){
                    productions.add(new Production(key,production));
                }
            }
        }
        cfgIn.setEditable(true);
        var.setCellValueFactory(new PropertyValueFactory<Production, String>("name"));
        go.setCellValueFactory(new PropertyValueFactory<Production, String>("go"));
        rule.setCellValueFactory(new PropertyValueFactory<Production, String>("rule"));
        cfgIn.setItems(new ObservableListWrapper<>(productions));
    }
}
