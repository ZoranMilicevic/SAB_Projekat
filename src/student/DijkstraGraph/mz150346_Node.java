package student.DijkstraGraph;

import student.mz150346_Order;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class mz150346_Node {
    private int id;
    private Map<mz150346_Node, Integer> adjacentNodes = new HashMap<>();
    private List<mz150346_Node> shortestPath = new LinkedList<>();
    private int distance = Integer.MAX_VALUE;

    public mz150346_Node(int id){
        this.id = id;
    }

    public void addAdjecent(int nodeId, int distance){
        mz150346_Node k = mz150346_Order.graph.getNodes().get(nodeId);
        adjacentNodes.put(k, distance);
    }




    //GETTERS AND SETTERS

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<mz150346_Node> getShortestPath() {
        return shortestPath;
    }

    public void setShortestPath(List<mz150346_Node> shortestPath) {
        this.shortestPath = shortestPath;
    }

    public Map<mz150346_Node, Integer> getAdjacentNodes() {
        return adjacentNodes;
    }

    public void setAdjacentNodes(Map<mz150346_Node, Integer> adjacentNodes) {
        this.adjacentNodes = adjacentNodes;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
