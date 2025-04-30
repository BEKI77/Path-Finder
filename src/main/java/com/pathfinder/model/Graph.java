package com.pathfinder.model;

import java.util.*;

public class Graph {
    Map<Node, List<Edge>> adjacencyList;

    // Find nearest node given a lat, lng
    Node findNearest(double lat, double lng) {
        return new Node();
    }

    // Dijkstra's algorithm
    // List<double[]> findShortestPath(double startLat, double startLng, double
    // endLat, double endLng) {
    // return new List(new List())
    // }
}

class Node {
    double lat;
    double lng;
}

class Edge {
    Node to;
    double weight;
}
