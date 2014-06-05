package cfg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

public class CYKParser {
    private HashMap<String, Set<TreeMap<Integer, Integer>>>[][] parseTable;
    private CFG grammar;
    private char[]  wordTerminals;

    public CYKParser(CFG grammar){this.grammar = grammar;}

    public HashMap<String, Set<TreeMap<Integer, Integer>>>[][] getParseTable() {return parseTable;}

    public char[] getWordTerminals() {return wordTerminals;}

    public CFG getGrammar() {return grammar;}

    public boolean parse(String word){
        this.wordTerminals = word.toCharArray();
        parseTable = (HashMap<String, Set<TreeMap<Integer, Integer>>>[][])new HashMap[word.length()][word.length()];
        for(int i = 0; i < parseTable[0].length; i++){
            for(String key : grammar.getProductions().keySet()){
                for(String production : grammar.getProductions().get(key)){
                    if(String.valueOf(wordTerminals[i]).equals(production)){
                        if(parseTable[0][i] == null){
                            parseTable[0][i] = new HashMap<>();
                        }
                        parseTable[0][i].put(key, new HashSet<TreeMap<Integer, Integer>>());
                    }
                }
            }
        }
        for(int n = 0; n < word.length(); n++){
            for(int i = 0; i < word.length()-n-1; i++){
                for(int j = 0; j < n+1; j++){
                    HashMap<String, Set<TreeMap<Integer, Integer>>> possibleProductions = new HashMap<>();
                    if(parseTable[j][i] != null && parseTable[n-j][j+i+1]!=null)
                        for(String production : parseTable[j][i].keySet()){
                            for(String production1 : parseTable[n-j][j+i+1].keySet()){
                                possibleProductions.put(production+production1, new HashSet<TreeMap<Integer, Integer>>());
                                TreeMap<Integer, Integer> ind = new TreeMap<>();
                                ind.put(j, i);
                                possibleProductions.get(production+production1).add(ind);
                                ind = new TreeMap<>();
                                ind.put(n - j, j + i + 1);
                                possibleProductions.get(production+production1).add(ind);
                            }
                        }
                    for(String possible : possibleProductions.keySet()){
                        for(String key : grammar.getProductions().keySet()){
                            for(String production : grammar.getProductions().get(key)){
                                if(production.equals(possible)){
                                    if(parseTable[n+1][i] == null){
                                        parseTable[n+1][i] = new HashMap<>();
                                    }
                                    parseTable[n+1][i].put(key, new HashSet<>(possibleProductions.get(possible)));
                                }
                            }
                        }
                    }
                }
            }
        }
        printParseTable();
        return parseTable[word.length() - 1][0] != null &&
               parseTable[word.length() - 1][0].containsKey(grammar.getStartSymbol());
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
