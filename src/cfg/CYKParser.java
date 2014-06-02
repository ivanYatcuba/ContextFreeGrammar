package cfg;

import java.util.HashSet;
import java.util.Set;

public class CYKParser {
    private Set<String>[][] parseTable;
    private CFG grammar;

    public CYKParser(CFG grammar){
        this.grammar = grammar;

    }

    public boolean parse(String word){
        char[]  wordTerminals = word.toCharArray();
        parseTable = (HashSet<String>[][])new Set[word.length()][word.length()];
        for(int i = 0; i < parseTable[0].length; i++){
            for(String key : grammar.getProductions().keySet()){
                for(String production : grammar.getProductions().get(key)){
                    if(String.copyValueOf(wordTerminals[i]))
                }
            }
        }
    }
}
