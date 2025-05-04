package com.pathfinder.model;

import java.util.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
            double endClickedLat) {
        Point tmpStart = new Point(startClickedLat, startClickedLon);
        Point tmpEnd = new Point(endClickedLat, endClickedLon);

        Point start = findNearestNode(tmpStart);
        Point end = findNearestNode(tmpEnd);

        List<List<Point>> allPaths = new ArrayList<>();
        dijkstra(start, end, allPaths);

        return allPaths;
    }

    public void dfs(Point start, Point end, List<List<Point>> allPaths) {
        Stack<PathState> stack = new Stack<>();
        Set<Point> visited = new HashSet<>();
        visited.add(start);

        stack.push(new PathState(start, new ArrayList<>()));

        while (!stack.isEmpty()) {
            PathState currentState = stack.pop();
            Point current = currentState.current;
            List<Point> path = currentState.path;

            path.add(current);

            if (current.equals(end)) {
                allPaths.add(path);
                return;
            } else {
                for (WightedEdge neighbor : graph.get(current)) {
                    if (!visited.contains(neighbor.target)) {
                        visited.add(neighbor.target);
                        List<Point> newPath = new ArrayList<>(path);
                        stack.push(new PathState(neighbor.target, newPath));
                    }
                }
            }
        }

    }

    public void dijkstra(Point start, Point end, List<List<Point>> allPaths) {
        Map<Point, Double> distances = new HashMap<>();
        PriorityQueue<Point> pq = new PriorityQueue<>(Comparator.comparingDouble(distances::get));
        Map<Point, Point> previous = new HashMap<>();

        for (Point node : graph.keySet()) {
            distances.put(node, Double.MAX_VALUE);
        }
        distances.put(start, 0.0);
        pq.add(start);

        while (!pq.isEmpty()) {
            Point current = pq.poll();

            for (WightedEdge edge : graph.get(current)) {
                double newDist = distances.get(current) + edge.weight;
                if (newDist < distances.get(edge.target)) {
                    distances.put(edge.target, newDist);
                    previous.put(edge.target, current);
                    pq.add(edge.target);
                }
            }
        }

        List<Point> path = new ArrayList<>();
        for (Point at = end; at != null; at = previous.get(at)) {
            path.add(at);
        }

        this.totDistance = distances.get(end);
        System.out.println("Total distance: " + distances.get(end));

        Collections.reverse(path);
        allPaths.add(path);
    }

    public String convertPathsToGeoJson(List<List<Point>> allPaths) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode featureCollection = mapper.createObjectNode();
        featureCollection.put("type", "FeatureCollection");

        ArrayNode featuresArray = mapper.createArrayNode();

        for (List<Point> path : allPaths) {
            ObjectNode feature = mapper.createObjectNode();
            feature.put("type", "Feature");

            // Geometry object
            ObjectNode geometry = mapper.createObjectNode();
            geometry.put("type", "LineString");
            ArrayNode coordinates = mapper.createArrayNode();

            for (Point point : path) {
                ArrayNode coord = mapper.createArrayNode();
                coord.add(point.lon); // GeoJSON = [lon, lat]
                coord.add(point.lat);
                coordinates.add(coord);
            }

            geometry.set("coordinates", coordinates);
            feature.set("geometry", geometry);

            // Optional: add properties
            ObjectNode properties = mapper.createObjectNode();
            properties.put("pathLength", path.size());
            feature.set("properties", properties);

            featuresArray.add(feature);
        }

        featureCollection.set("features", featuresArray);

        try {
            return mapper.writeValueAsString(featureCollection);
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the Earth in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in kilometers
    }
}

class PathState {
    Point current;
    List<Point> path;

    PathState(Point current, List<Point> path) {
        this.current = current;
        this.path = path;
    }
}