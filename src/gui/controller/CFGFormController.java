package gui.controller;


import cfg.CFG;
import cfg.CYKParser;
import cfg.tree.DerivationTreeBuilder;
import cfg.tree.TreeLayoutViewer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;



public class CFGFormController implements Initializable {

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
    CFG cfg = new CFG();
    public void buildCFG(){

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

    public void showCYK(){
        JFrame frame = new JFrame("CFG");
        frame.setLocationRelativeTo(null);
        final JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);
        frame.setSize(600, 400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        final CYKTableController controller = new CYKTableController();
        CYKParser parser = new CYKParser(cfg);
        String parseWord = word.getText();
        if(parseWord.isEmpty()) parseWord = "$";
        parser.parse(parseWord);
        controller.setCykParser(parser);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                createScene(fxPanel, "../fxml/cyk.fxml", controller);
            }
        });
    }



    public void clearData(){
        data.removeAll(data);
        Production start = new Production("S", "");
        data.add(start);
    }

    public void openChomsky(){
        // This method is invoked on the EDT thread
        JFrame frame = new JFrame("CFG");
        frame.setLocationRelativeTo(null);
        final JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);
        frame.setSize(600, 400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        final ChomskyTableController controller = new ChomskyTableController();
        cfg.toChomskyForm();
        controller.setCfg(cfg);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                createScene(fxPanel, "../fxml/chomsky.fxml", controller);
            }
        });
    }


    private  void createScene(JFXPanel fxPanel, String path, Object controller) {

        Parent root = null;
        try {

            FXMLLoader loader = new FXMLLoader(CFGFormController.class.getResource(path));
            loader.setController(controller);
            root = (Parent)loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root, 392, 283);

        scene.getStylesheets().add("gui/fxml/style.css");
        fxPanel.setScene(scene);
    }

}
