package cfg.tree;

import cfg.CYKParser;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.Forest;

import java.util.TreeMap;

public class DerivationTreeBuilder {
    private Forest<String, Integer> graph= new DelegateForest<>(new DirectedOrderedSparseMultigraph<String, Integer>());

    private CYKParser cykParser;
    private int edgeNum = 0;

    public Forest<String, Integer> getGraph() {return graph;}

    public DerivationTreeBuilder(CYKParser cykParser){
        this.cykParser = cykParser;

        graph.addVertex(cykParser.getGrammar().getStartSymbol());

        String currentVertex = cykParser.getGrammar().getStartSymbol();
        int l = cykParser.getParseTable().length;
        for(TreeMap<Integer, Integer> ind : cykParser.getParseTable()[l-1][0].get(currentVertex)){
            for(Integer i : ind.keySet()){
                addVertex(currentVertex, i, ind.get(i));
            }
        }

    }

    public void addVertex(String parent, int i, int j){
        for(String vertex : cykParser.getParseTable()[i][j].keySet()){
            String vertexName = edgeNum + "|" + vertex;
            graph.addVertex(vertexName);
            graph.addEdge(edgeNum++, parent, vertexName);

            if(cykParser.getParseTable()[i][j].get(vertex).isEmpty()){
                String terminalVertex = edgeNum + "|" + String.valueOf(cykParser.getWordTerminals()[j]);
                graph.addVertex(terminalVertex);
                graph.addEdge(edgeNum++, vertexName, terminalVertex);
            }else {
                for(TreeMap<Integer, Integer> ind : cykParser.getParseTable()[i][j].get(vertex)){
                    for(Integer itr : ind.keySet()){
                        addVertex(vertexName, itr, ind.get(itr));
                    }
                }
            }
        }
    }




}