package cfg;

import java.util.HashSet;
import java.util.Set;

public class CYKParser {
    private Set<String>[][] parseTable;
    private CFG grammar;

    public CYKParser(CFG grammar){this.grammar = grammar;}

    public boolean parse(String word){
        char[]  wordTerminals = word.toCharArray();
        parseTable = (HashSet<String>[][])new HashSet[word.length()][word.length()];
        for(int i = 0; i < parseTable[0].length; i++){
            for(String key : grammar.getProductions().keySet()){
                for(String production : grammar.getProductions().get(key)){
                    if(String.valueOf(wordTerminals[i]).equals(production)){
                        if(parseTable[0][i] == null){
                            parseTable[0][i] = new HashSet<>();
                        }
                        parseTable[0][i].add(key);
                    }
                }
            }
        }
        for(int n = 0; n < word.length(); n++){
            for(int i = 0; i < word.length()-n-1; i++){
                for(int j = 0; j < n+1; j++){
                    Set<String> possibleProductions = new HashSet<>();
                    if(parseTable[j][i] != null && parseTable[n-j][j+i+1]!=null)
                        for(String production : parseTable[j][i]){
                            for(String production1 : parseTable[n-j][j+i+1]){
                                possibleProductions.add(production+production1);
                            }
                        }
                    for(String possible : possibleProductions){
                        for(String key : grammar.getProductions().keySet()){
                            for(String production : grammar.getProductions().get(key)){
                                if(production.equals(possible)){
                                    if(parseTable[n+1][i] == null){
                                        parseTable[n+1][i] = new HashSet<>();
                                    }
                                    parseTable[n+1][i].add(key);
                                }
                            }
                        }
                    }
                }
            }
        }
        printParseTable();
        return parseTable[word.length() - 1][0] != null &&
               parseTable[word.length() - 1][0].contains(grammar.getStartSymbol());
    }

    private void printParseTable(){
        for(int i =0 ; i< parseTable.length; i++){
            for(int j = 0; j<parseTable[i].length; j++){
                System.out.print(parseTable[i][j]+",");
            }
            System.out.println();
        }
    }
}
