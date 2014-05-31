package gui.controller;


import cfg.CFG;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.net.URL;
import java.util.*;

public class CFGFormController implements Initializable {

    public class Production{
        private String name;
        private String rule;
        private final String GO = "->";

        private Production() {}
        private Production(String name, String rule) {
            this.name = name;
            this.rule = rule;
        }

        public String getName() {return name;}
        public void setName(String name) {this.name = name;}

        public String getRule() {return rule;}
        public void setRule(String rule) {this.rule = rule;}

        public String getGo() {return GO;}

        @Override
        public String toString() {
            return "Production{" +
                    "name='" + name + '\'' +
                    ", rule='" + rule + '\'' +
                    ", GO='" + GO + '\'' +
                    '}';
        }
    }

    private ObservableList<Production> data = FXCollections.observableArrayList();

    @FXML
    private TableView<Production> cfgIn;

    @FXML
    private TableColumn<Production, String> var;
    @FXML
    private TableColumn<Production, String> go;
    @FXML
    private TableColumn<Production, String> rule;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        cfgIn.setEditable(true);

        var.setCellValueFactory(new PropertyValueFactory<Production, String>("name"));
        var.setCellFactory(TextFieldTableCell.<Production>forTableColumn());

        go.setCellValueFactory(new PropertyValueFactory<Production, String>("go"));
        go.setEditable(false);

        rule.setCellValueFactory(new PropertyValueFactory<Production, String>("rule"));
        rule.setCellFactory(TextFieldTableCell.<Production>forTableColumn());
        rule.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Production, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Production, String> productionStringCellEditEvent) {
                productionStringCellEditEvent.getRowValue().setRule(productionStringCellEditEvent.getNewValue());
                if (productionStringCellEditEvent.getTablePosition().getRow() == data.size() - 1) {
                    data.add(new Production("A" + data.size(), ""));
                }
            }
        });

        Production start = new Production("S", "");
        data.add(start);

        cfgIn.setItems(data);
    }

    public void buildCFG(){
        CFG cfg = new CFG();
        if(data.get(data.size()-1).getRule().isEmpty()){
            data.remove(data.size()-1);
        }
        HashMap<String, Set<String>> productions = new HashMap<>();
        for(Production production : data){
            String name = production.getName();
            Set<String> productionRules = new HashSet<>();
            if(production.getRule().contains("|")){
                String[] rules = production.getRule().split("\\|");
                Collections.addAll(productionRules, rules);
            }else {
                productionRules.add(production.getRule());
            }
            productions.put(name, productionRules);
        }
        cfg.setProductions(productions);
        cfg.setStartSymbol("S");
        Set<String> terminals = new HashSet<>();
        for(String prod : productions.keySet()){
            for(String t : productions.get(prod)){
                for(String key : productions.keySet()){
                    t = t.replaceAll(key,"");
                }
                char[] potentialTerminals = t.toCharArray();
                for(char c : potentialTerminals){
                    terminals.add(Character.toString(c));
                }
            }
        }
        cfg.setTerminals(terminals);
        System.out.print(cfg);
    }

}
