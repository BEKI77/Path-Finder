package com.pathfinder.model;

import java.util.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class Graph {

    private static Map<Point, List<WightedEdge>> graph = new HashMap<>();
    public double totDistance;

    private void addEdge(Point a, Point b) {
        double weight = haversine(a.lat, a.lon, b.lat, b.lon);
        graph.computeIfAbsent(a, k -> new ArrayList<>()).add(new WightedEdge(b, weight));
        graph.computeIfAbsent(b, k -> new ArrayList<>()).add(new WightedEdge(a, weight));
    }

    private void processLineString(JsonNode coords) {
        for (int i = 0; i < coords.size() - 1; i++) {
            Point a = new Point(coords.get(i).get(1).asDouble(), coords.get(i).get(0).asDouble()); // [lon, lat]
            Point b = new Point(coords.get(i + 1).get(1).asDouble(), coords.get(i + 1).get(0).asDouble());
            addEdge(a, b);
        }
    }

    public void buildFromGeoJson(ArrayNode features) {
        for (JsonNode feature : features) {
            JsonNode geometry = feature.get("geometry");

            if (geometry == null || geometry.get("coordinates") == null)
                continue;

            String type = geometry.get("type").asText();

            switch (type) {
                case "LineString" -> {
                    processLineString(geometry.get("coordinates"));
                }
                case "MultiLineString" -> {
                    for (JsonNode line : geometry.get("coordinates")) {
                        processLineString(line);
                    }
                }
                default -> {
                }
            }
        }

    }

    public Point findNearestNode(Point clicked) {
        Point nearest = null;
        double minDist = Double.MAX_VALUE;
        for (Point node : graph.keySet()) {
            double dist = Math.pow(clicked.lat - node.lat, 2) + Math.pow(clicked.lon - node.lon, 2);
            if (dist < minDist) {
                minDist = dist;
                nearest = node;
            }
        }
        return nearest;
    }

    public List<List<Point>> findAllPaths(double startClickedLon, double startClickedLat, double endClickedLon,
            double endClickedLat, boolean fl) {
        Point tmpStart = new Point(startClickedLat, startClickedLon);
        Point tmpEnd = new Point(endClickedLat, endClickedLon);

        Point start = findNearestNode(tmpStart);
        Point end = findNearestNode(tmpEnd);

        List<List<Point>> allPaths = new ArrayList<>();

        if (fl) {
            dijkstra(start, end, allPaths);
        } else {
            dfs(start, end, allPaths);
        }

        return allPaths;
    }

    public void dfs(Point start, Point end, List<List<Point>> allPaths) {
        Stack<PathState> stack = new Stack<>();
        Set<Point> visited = new HashSet<>();
        visited.add(start);

        stack.push(new PathState(start, new ArrayList<>(), 0.0));

        while (!stack.isEmpty()) {
            PathState currentState = stack.pop();
            Point current = currentState.current;
            List<Point> path = currentState.path;
            double cost = currentState.cost;

            path.add(current);

            if (current.equals(end)) {
                System.out.println(cost);
                this.totDistance = cost;
                allPaths.add(path);
                return;
            }

            for (WightedEdge neighbor : graph.get(current)) {
                if (!visited.contains(neighbor.target)) {
                    visited.add(neighbor.target);
                    List<Point> newPath = new ArrayList<>(path);
                    stack.push(new PathState(neighbor.target, newPath, cost + neighbor.weight));
                }
            }
        }

    }

    public void dijkstra(Point start, Point end, List<List<Point>> allPaths) {
        PriorityQueue<PathState> pq = new PriorityQueue<>(Comparator.comparingDouble(state -> state.cost));
        Set<Point> visited = new HashSet<>();

        pq.add(new PathState(start, new ArrayList<>(), 0.0));

        while (!pq.isEmpty()) {
            PathState currentState = pq.poll();
            Point current = currentState.current;
            List<Point> path = currentState.path;
            double cost = currentState.cost;

            if (visited.contains(current))
                continue;
            visited.add(current);

            path.add(current);

            if (current.equals(end)) {
                allPaths.add(path);
                this.totDistance = cost;
                return;
            }

            for (WightedEdge neighbor : graph.get(current)) {
                if (!visited.contains(neighbor.target)) {
                    List<Point> newPath = new ArrayList<>(path);
                    pq.add(new PathState(neighbor.target, newPath, cost + neighbor.weight));
                }
            }
        }
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the Earth in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = (Math.sin(dLat / 2) * Math.sin(dLat / 2)) + (Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in kilometers
    }
}

class PathState {
    Point current;
    List<Point> path;
    double cost;

    PathState(Point current, List<Point> path, double cost) {
        this.current = current;
        this.path = path;
        this.cost = cost;
    }
}