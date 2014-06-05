package cfg;

import java.util.*;

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

    public void toChomskyForm(){
        removeEmptyProduction();
        toNoUnitProductions();
        toUnusedRemoved();
        toSingleTerminal();
        toPairVariable();
    }

    private void toSingleTerminal(){

        for(String terminal : terminals){

            String name = RandomNameGenerator.generate(productions.keySet(), terminals);


            boolean productionCreated = false;
            for(String var : productions.keySet()){
                Set<String> newRules = new HashSet<>();
                for(String production : productions.get(var)){
                    String temp = production;
                    for(String key : productions.keySet()){
                        temp = temp.replaceAll(key,"");
                    }
                    if(temp.contains(terminal) && production.length()>=2){
                        productionCreated = true;
                        String str = production;
                        while (str.contains(terminal)){
                            str = str.replace(terminal, name);
                        }
                        newRules.add(str);
                    } else {
                        newRules.add(production);
                    }
                }
                productions.get(var).removeAll(productions.get(var));
                productions.get(var).addAll(newRules);
            }

            if(productionCreated){
                productions.put(name, new HashSet<String>());
                productions.get(name).add(terminal);
            }

        }
    }

    private void toPairVariable(){
        HashMap<String, Set<String>> newProductions = new HashMap<>();
        for(String var : productions.keySet()) {

            Set<String> newRules = new HashSet<>(productions.get(var));
            for(String production : productions.get(var)){
                int count = 0;
                StringBuilder rule = new StringBuilder();
                StringBuilder outProd = new StringBuilder();
                String newProd;

                for(int i = 0; i < production.length(); i++){
                    rule.append(production.charAt(i));
                    if(productions.keySet().contains(rule.toString())){
                        count++;
                        if(count < 2){
                            outProd.append(rule.toString());
                        }
                        rule = new StringBuilder();
                    }
                    if(count > 2){
                        newRules.remove(production);
                        newProd = production.substring(outProd.length(), production.length());
                        String name = RandomNameGenerator.generate(productions.keySet(), terminals);
                        for(String k : productions.keySet()){
                            if(name.contains(k)){
                                name = RandomNameGenerator.generate(productions.keySet(), terminals);
                            }
                        }
                        outProd.append(name);
                        newRules.add(outProd.toString());
                        newProductions.put(name, new HashSet<String>());
                        newProductions.get(name).add(newProd);
                        break;
                    }
                }
            }
            productions.get(var).removeAll(productions.get(var));
            productions.get(var).addAll(newRules);

        }
        productions.putAll(newProductions);
        if(!newProductions.isEmpty()){
            toPairVariable();
        }
    }

    private void replaceStart(){
        String newStart = RandomNameGenerator.generate(productions.keySet(), terminals);
        productions.put(newStart, new HashSet<>(productions.get(startSymbol)));
        productions.get(startSymbol).removeAll(productions.get(startSymbol));
        productions.get(startSymbol).add(newStart);
        for(String var : productions.keySet()){

            if(!var.equals(startSymbol)){
                Set<String> newRules = new HashSet<>();
                for(String rule : productions.get(var)){
                    if(rule.contains(startSymbol)){
                        newRules.add(rule.replaceAll(startSymbol, newStart));
                    }else {
                        newRules.add(rule);
                    }
                }
                productions.get(var).removeAll(productions.get(var));
                productions.get(var).addAll(newRules);
            }

        }
    }

    private void removeEmptyProduction(){
        boolean change = false;
        HashMap<String, Set<String>> newProductions = new HashMap<>();
        for(String t : productions.keySet()){
            newProductions.put(t, new HashSet<>(productions.get(t)));
        }
        for(String var : productions.keySet()){
            for(String production : productions.get(var)){
                if(production.contains("$") && production.length()<2){

                    for(String key : productions.keySet()){
                        for(String keyProduction : productions.get(key)){
                            if(keyProduction.contains(var)){
                                productionFix(newProductions, var, "");
                            }
                        }
                    }
                    if(!var.equals(startSymbol)){
                        change = true;
                        newProductions.get(var).remove(production);
                    }
                } if(production.contains("$") && production.length()>2){
                    newProductions.get(var).remove(production);
                    newProductions.get(var).add(production.replaceAll("\\$", ""));
                }
            }
        }

       List<String> keysToRemove = new ArrayList<>();
        for(String key : newProductions.keySet()){
            if(newProductions.get(key).isEmpty()){
                keysToRemove.add(key);
            }
        }
        for(String key : keysToRemove){
            newProductions.remove(key);
        }

        HashMap<String, Set<String>> newNewProductions = new HashMap<>();
        for(String t : newProductions.keySet()){
            newNewProductions.put(t, new HashSet<>(newProductions.get(t)));
        }
        for(String key : newProductions.keySet()){
            for(String production : newProductions.get(key)){
                for(String toRemove : keysToRemove){
                    if(production.contains(toRemove)){
                        newNewProductions.get(key).remove(production);
                        if(production.replace(toRemove, "").isEmpty())newNewProductions.get(key).add("$");
                    }
                }
            }
        }
        productions = newNewProductions;
        //if(change) removeEmptyProduction();
    }

    private void toNoUnitProductions(){
        HashMap<String, Set<String>> newProductions = new HashMap<>(productions);
        for(String key : productions.keySet()){
            newProductions.put(key, new HashSet<String>());
            for(String prod : productions.get(key)){
                newProductions.get(key).add(prod);
            }
        }
        boolean changes = false;
        for(String var : productions.keySet()){
            for(String rule : productions.get(var)){
                for(String key : productions.keySet()){
                    if(rule.contains(key)){
                        if(rule.replace(key,"").isEmpty()){
                            changes = true;
                            if(var.equals(key)){newProductions.get(var).remove(rule);}
                            else {
                                newProductions.get(var).remove(rule);
                                for(String production : productions.get(key)){newProductions.get(var).add(production);}
                            }
                        }
                    }
                }
            }
            if(changes){break;}
        }
        productions = newProductions;
        if(changes)toNoUnitProductions();
    }

    private void toUnusedRemoved(){
        HashMap<String, Set<String>> unused = new HashMap<>(productions);
        removeUnusedRecursevly(startSymbol, unused);
        for (String trash : unused.keySet()){
            productions.remove(trash);
        }
    }
    private void removeUnusedRecursevly(String start, HashMap<String, Set<String>> unused){
        unused.remove(start);
        for(String production : productions.get(start)){
            for(String key : productions.keySet()){
                if(production.contains(key) && unused.containsKey(key)){
                    removeUnusedRecursevly(key, unused);
                }
            }
        }
    }

    private void productionFix(HashMap<String, Set<String>> newProductions, String key, String r){
        for(String innerKey : productions.keySet()){
            for(String production : productions.get(innerKey)){
                StringBuilder temp = new StringBuilder(production);
                while(temp.toString().contains(key)){

                    temp =  temp.replace(temp.indexOf(key),
                            temp.indexOf(key) + key.length(), r);
                    if(temp.toString().isEmpty()){
                        newProductions.get(innerKey).add("$");
                    }else {
                        newProductions.get(innerKey).add(temp.toString());
                    }
                }

                temp = new StringBuilder(production);
                while(temp.toString().contains(key)){
                    temp =  temp.replace(temp.lastIndexOf(key),
                            temp.lastIndexOf(key) + key.length(), r);
                    if(temp.toString().isEmpty()){
                        newProductions.get(innerKey).add("$");
                    }else {
                        newProductions.get(innerKey).add(temp.toString());
                    }
                }
            }
        }
    }

}
