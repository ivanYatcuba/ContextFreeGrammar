package cfg;

import java.util.HashMap;
import java.util.Set;

public class CFG {
    private Set<String> terminals;
    private HashMap<String, Set<String>> productions;
    private String startSymbol;

    public Set<String> getTerminals() {return terminals;}
    public void setTerminals(Set<String> terminals) {this.terminals = terminals;}

    public HashMap<String, Set<String>> getProductions() {return productions;}
    public void setProductions(HashMap<String, Set<String>> productions) {this.productions = productions;}

    public String getStartSymbol() {return startSymbol;}
    public void setStartSymbol(String startSymbol) {this.startSymbol = startSymbol;}

    @Override
    public String toString() {
        return "CFG{" +
                "terminals=" + terminals +
                ", productions=" + productions +
                ", startSymbol='" + startSymbol + '\'' +
                '}';
    }
}
