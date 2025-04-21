package model;

import java.util.*;

public class Graph {
    private final Map<Node, List<Edge>> adjacencyList = new HashMap<>();
    private final Map<String, Node> nodeIdMap = new HashMap<>();

    public void addNode(Node node) {
        adjacencyList.putIfAbsent(node, new ArrayList<>());
        nodeIdMap.put(node.getId(), node);
    }

    public void addEdge(Node source, Node destination, double distance) {
        addNode(source);
        addNode(destination);
        adjacencyList.get(source).add(new Edge(destination, distance));
        // For undirected graph:
        adjacencyList.get(destination).add(new Edge(source, distance));
    }

    public List<Node> findShortestPath(Node start, Node end) {
        // Dijkstra's algorithm implementation
        Map<Node, Double> distances = new HashMap<>();
        Map<Node, Node> previousNodes = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(
                Comparator.comparingDouble(distances::get));

        // Initialize distances
        for (Node node : adjacencyList.keySet()) {
            distances.put(node, Double.MAX_VALUE);
        }
        distances.put(start, 0.0);
        queue.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (current.equals(end)) {
                break; // Found the shortest path
            }

            for (Edge edge : adjacencyList.get(current)) {
                Node neighbor = edge.getTarget();
                double newDistance = distances.get(current) + edge.getDistance();

                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    previousNodes.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        // Reconstruct path
        List<Node> path = new ArrayList<>();
        for (Node node = end; node != null; node = previousNodes.get(node)) {
            path.add(node);
        }
        Collections.reverse(path);

        return path.isEmpty() || !path.get(0).equals(start) ? null : path;
    }

    public Set<Node> getNodes() {
        return adjacencyList.keySet();
    }

    public Node getNodeById(String id) {
        return nodeIdMap.get(id); // Retrieve the node by its ID
    }

    public List<List<Node>> findAllPaths(Node start, Node end) {
        List<List<Node>> allPaths = new ArrayList<>();
        List<Node> currentPath = new ArrayList<>();
        Set<Node> visited = new HashSet<>();

        dfs(start, end, visited, currentPath, allPaths);
        return allPaths;
    }

    private void dfs(Node current, Node end, Set<Node> visited, List<Node> currentPath, List<List<Node>> allPaths) {
        visited.add(current);
        currentPath.add(current);

        if (current.equals(end)) {
            // If we reached the destination, add the current path to allPaths
            allPaths.add(new ArrayList<>(currentPath));
        } else {
            // Explore neighbors
            for (Edge edge : adjacencyList.getOrDefault(current, Collections.emptyList())) {

                Node neighbor = edge.getTarget();

                if (!visited.contains(neighbor)) {
                    dfs(neighbor, end, visited, currentPath, allPaths);
                }
            }
        }

        // Backtrack
        visited.remove(current);
        currentPath.remove(currentPath.size() - 1);
    }
}