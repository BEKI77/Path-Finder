package com.pathfinder;

// import java.util.List;

public class JavaConnector {
    public int count = 0;

    public String logCoordinates(Number x, Number y) {
        this.count += 1;
        System.out.println("Clicked coordinates: X=" + x + ", Y=" + y);
        return "Clicked coordinates: X=" + x + ", Y=" + y;
    }

    public void log(String message) {
        System.out.println("[JS Console] " + message);
    }

    public int getCount() { // Public getter
        return count;
    }

    // public String computeRoute(double startLat, double startLng, double endLat,
    // double endLng) {
    // // 1. Find nearest nodes in the graph
    // // 2. Run Dijkstra/A* to find shortest path
    // // 3. Return the list of coordinates as a JSON string

    // List<double[]> path = yourGraph.findShortestPath(startLat, startLng, endLat,
    // endLng);

    // StringBuilder json = new StringBuilder("[");
    // for (double[] point : path) {
    // json.append("[").append(point[0]).append(",").append(point[1]).append("],");
    // }
    // if (path.size() > 0)
    // json.setLength(json.length() - 1);
    // json.append("]");
    // return json.toString();
    // }
}
