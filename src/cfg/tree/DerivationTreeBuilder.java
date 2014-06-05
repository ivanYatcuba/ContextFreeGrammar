package cfg.tree;

import cfg.CYKParser;
import edu.uci.ics.jung.graph.DelegateForest;

import java.util.HashMap;

public class DerivationTreeBuilder {
    private DelegateForest<String, Integer> graph= new DelegateForest<>();

    private CYKParser cykParser;
    private int edgeNum = 0;

    public DelegateForest<String, Integer> getGraph() {return graph;}

    public DerivationTreeBuilder(CYKParser cykParser){
        this.cykParser = cykParser;

        graph.addVertex(cykParser.getGrammar().getStartSymbol());

        String currentVertex = cykParser.getGrammar().getStartSymbol();
        int l = cykParser.getParseTable().length;
        for(HashMap<Integer, Integer> ind : cykParser.getParseTable()[l-1][0].get(currentVertex)){
            for(Integer i : ind.keySet()){
                addVertex(currentVertex, i, ind.get(i));
            }
        }

    }

    public void addVertex(String parent, int i, int j){
        for(String vertex : cykParser.getParseTable()[i][j].keySet()){
            String vertexName = vertex+'|'+edgeNum;
            graph.addVertex(vertexName);
            graph.addEdge(edgeNum++, parent, vertexName);

            if(cykParser.getParseTable()[i][j].get(vertex).isEmpty()){
                String terminalVertex =  String.valueOf(cykParser.getWordTerminals()[j])+'|'+edgeNum;
                graph.addVertex(terminalVertex);
                graph.addEdge(edgeNum++, vertexName, terminalVertex);
            }else {
                for(HashMap<Integer, Integer> ind : cykParser.getParseTable()[i][j].get(vertex)){
                    for(Integer itr : ind.keySet()){
                        addVertex(vertexName, itr, ind.get(itr));
                    }
                }
            }
        }
    }




}
