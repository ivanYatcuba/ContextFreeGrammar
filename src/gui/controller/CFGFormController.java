package gui.controller;


import cfg.CFG;
import cfg.CYKParser;
import cfg.tree.DerivationTreeBuilder;
import cfg.tree.TreeLayoutViewer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.swing.*;
import java.awt.*;
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

    @FXML
    private TextField word;

    @FXML
    private ImageView stats;

    private final String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private DerivationTreeBuilder treeDrawer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        word.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                if(!data.get(0).getRule().isEmpty()){
                    buildCFG();
                }
            }
        });

        cfgIn.setEditable(true);

        var.setCellValueFactory(new PropertyValueFactory<Production, String>("name"));
        var.setCellFactory(TextFieldTableCell.<Production>forTableColumn());
        var.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Production, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Production, String> productionStringCellEditEvent) {
                productionStringCellEditEvent.getRowValue().setName(productionStringCellEditEvent.getNewValue());
            }
        });

        go.setCellValueFactory(new PropertyValueFactory<Production, String>("go"));
        go.setEditable(false);

        rule.setCellValueFactory(new PropertyValueFactory<Production, String>("rule"));
        rule.setCellFactory(TextFieldTableCell.<Production>forTableColumn());
        rule.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Production, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Production, String> productionStringCellEditEvent) {
                productionStringCellEditEvent.getRowValue().setRule(productionStringCellEditEvent.getNewValue());
                if (productionStringCellEditEvent.getTablePosition().getRow() == data.size() - 1) {
                    if(data.size()-1 < lexicon.length()){
                        data.add(new Production(String.valueOf(lexicon.charAt(data.size()-1)) , ""));
                    }else {
                        //TO DO
                        //data.add(new Production(RandomNameGenerator.generate(null, null));
                    }

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
        cfg.setStartSymbol(data.get(0).getName());
        TreeSet<String> terminals = new TreeSet<>();
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


        cfg.toChomskyForm();
        System.out.println(cfg);
        CYKParser parser = new CYKParser(cfg);

        treeDrawer = null;
        String parseWord = word.getText();
        if(parseWord.isEmpty()) parseWord = "$";
        if(parser.parse(parseWord)){
            stats.setImage(new Image("gui/img/good.png"));
            treeDrawer = new DerivationTreeBuilder(parser);
        }else {
            stats.setImage(new Image("gui/img/bad.png"));
        }
    }


    public void drawTree(){
        if(treeDrawer != null){
           JFrame frame = new JFrame();
            Container content = frame.getContentPane();
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            content.add(new TreeLayoutViewer(treeDrawer));
            frame.pack();
            frame.setSize(500, 500);
            frame.setVisible(true);
        }
    }



    public void clearData(){
        data.removeAll(data);
        Production start = new Production("S", "");
        data.add(start);
    }


}
