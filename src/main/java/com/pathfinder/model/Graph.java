package com.pathfinder.model;

import java.util.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class Graph {

    private final Map<Point, List<Point>> graph = new HashMap<>();

    private void addEdge(Point a, Point b) {
        graph.computeIfAbsent(a, k -> new ArrayList<>()).add(b);
        graph.computeIfAbsent(b, k -> new ArrayList<>()).add(a);
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
                    break;
                }
                case "MultiLineString" -> {
                    for (JsonNode line : geometry.get("coordinates")) {
                        processLineString(line);
                    }
                    break;
                }
                default -> {
                }
            }
        }

        // System.out.println(graph);
    }

    public String chk(double x, double y) {
        Point nearest = null;
        double minDist = Double.MAX_VALUE;
        for (Point node : graph.keySet()) {
            double dist = Math.pow(x - node.lat, 2) + Math.pow(y - node.lon, 2);
            if (dist < minDist) {
                minDist = dist;
                nearest = node;
            }
        }

        System.out.println("Nearest Point: " + nearest.lat + " " + nearest.lon);
        Map<String, Double> result = new HashMap<>();
        result.put("lat", nearest.lat);
        result.put("lon", nearest.lon);

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(result);
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
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

    public List<List<Point>> findAllPaths(Point startClicked, Point endClicked) {
        Point start = findNearestNode(startClicked);
        Point end = findNearestNode(endClicked);

        List<List<Point>> allPaths = new ArrayList<>();
        dfs(start, end, new HashSet<>(), new ArrayList<>(), allPaths);
        return allPaths;
    }

    private void dfs(Point current, Point end, Set<Point> visited, List<Point> path, List<List<Point>> allPaths) {
        visited.add(current);
        path.add(current);

        if (current.equals(end)) {
            allPaths.add(new ArrayList<>(path));
        } else {
            for (Point neighbor : graph.getOrDefault(current, List.of())) {
                if (!visited.contains(neighbor)) {
                    dfs(neighbor, end, visited, path, allPaths);
                }
            }
        }

        path.remove(path.size() - 1);
        visited.remove(current);
    }
}
