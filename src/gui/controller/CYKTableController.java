package gui.controller;


import cfg.CYKParser;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class CYKTableController implements Initializable {
    @FXML
    private TextArea view;
    private CYKParser cykParser;

    public void setCykParser(CYKParser cykParser) {this.cykParser = cykParser;}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        StringBuilder sb = new StringBuilder();

        for(int i =0 ; i < cykParser.getParseTable().length; i++){
            for(int j = 0; j < cykParser.getParseTable()[i].length; j++){
                if(cykParser.getParseTable()[i][j] != null){
                    sb.append("{");
                    for(String p : cykParser.getParseTable()[i][j].keySet()){
                        sb.append(p).append(" ");
                    }
                    sb.append("} ");
                } else {
                    sb.append("NULL ");
                }

            }
            sb.append("\n");
        }
        view.setText(sb.toString());
        view.setEditable(false);
    }
}
