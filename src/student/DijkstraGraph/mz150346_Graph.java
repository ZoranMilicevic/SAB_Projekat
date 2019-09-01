package student.DijkstraGraph;

import java.util.*;

public class mz150346_Graph {
    private Map<Integer, mz150346_Node> nodes = new HashMap<>();

    public void addNode(mz150346_Node nodeA) {
        nodes.put(nodeA.getId(),nodeA);
    }

    public Map<Integer, mz150346_Node> getNodes() {
        return nodes;
    }

    public static mz150346_Graph calculateShortestPathFromSource(mz150346_Graph graph, mz150346_Node source) {
        source.setDistance(0);

        Set<mz150346_Node> settledNodes = new HashSet<>();
        Set<mz150346_Node> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (unsettledNodes.size() != 0) {
            mz150346_Node currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            for (Map.Entry<mz150346_Node, Integer> adjacencyPair : currentNode.getAdjacentNodes().entrySet()) {
                mz150346_Node adjacentNode = adjacencyPair.getKey();
                Integer edgeWeight = adjacencyPair.getValue();
                if (!settledNodes.contains(adjacentNode)) {
                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
        }
        return graph;
    }

    private static void calculateMinimumDistance(mz150346_Node evaluationNode, int edgeWeigh, mz150346_Node sourceNode) {
        Integer sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeigh);
            LinkedList<mz150346_Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }


    private static mz150346_Node getLowestDistanceNode(Set<mz150346_Node> unsettledNodes) {
        mz150346_Node lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (mz150346_Node node: unsettledNodes) {
            int nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }
}
