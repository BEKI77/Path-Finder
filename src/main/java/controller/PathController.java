package controller;

import model.*;
import view.MapPanel;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import javax.swing.*;
import java.util.List;

public class PathController {
    private final Graph graph;
    private final MapPanel mapPanel;
    private Node startNode;
    private Node endNode;

    public PathController(Graph graph, MapPanel mapPanel) {
        this.graph = graph;
        this.mapPanel = mapPanel;
        setupMapListeners();
    }

    private void setupMapListeners() {
        mapPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Coordinate coord = (Coordinate) mapPanel.getPosition(evt.getX(), evt.getY());

                Node clickedNode = null;
                double minDist = 100;
                System.out.println(coord);

                for (Node node : graph.getNodes()) {
                    double dist = distance(coord, node.getCoordinate());
                    if (dist < minDist) {
                        minDist = dist;
                        clickedNode = node;
                    }
                }

                System.out.println(clickedNode);

                if (clickedNode != null) {
                    if (SwingUtilities.isLeftMouseButton(evt)) {
                        startNode = clickedNode;
                    } else if (SwingUtilities.isRightMouseButton(evt)) {
                        endNode = clickedNode;
                    }

                    if (startNode != null && endNode != null) {
                        findAndDisplayPath();
                    }
                }
            }
        });
    }

    private void findAndDisplayPath() {
        List<List<Node>> allPaths = graph.findAllPaths(startNode, endNode); // Assume this method exists
        mapPanel.displayPaths(allPaths); // Update MapPanel to handle multiple paths

        if (allPaths != null && !allPaths.isEmpty()) {
            StringBuilder message = new StringBuilder("Available paths:\n");
            for (List<Node> path : allPaths) {
                double totalDistance = 0;
                for (int i = 1; i < path.size(); i++) {
                    totalDistance += distance(
                            path.get(i - 1).getCoordinate(),
                            path.get(i).getCoordinate());
                }
                message.append(String.format("Path: %s | Distance: %.2f km\n", path, totalDistance * 111));
            }
            JOptionPane.showMessageDialog(mapPanel, message.toString(), "Paths Found", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(mapPanel,
                    "No paths exist between the selected points",
                    "No Paths", JOptionPane.WARNING_MESSAGE);
        }
    }

    private double distance(Coordinate c1, Coordinate c2) {
        double lat1 = Math.toRadians(c1.getLat());
        double lon1 = Math.toRadians(c1.getLon());
        double lat2 = Math.toRadians(c2.getLat());
        double lon2 = Math.toRadians(c2.getLon());

        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;

        double a = Math.pow(Math.sin(dlat / 2), 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.pow(Math.sin(dlon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371 * c; // Earth's radius in km
    }
}