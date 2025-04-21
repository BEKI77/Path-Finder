import model.*;
import view.MapPanel;
import controller.PathController;
import javax.swing.*;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create and set up the window
            JFrame frame = new JFrame("OpenStreetMap Path Finder");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 700);

            // Create components
            MapPanel mapPanel = new MapPanel();
            Graph graph = createGraph();

            /// Set controller
            new PathController(graph, mapPanel);

            // Add components to frame
            frame.add(new JScrollPane(mapPanel), BorderLayout.CENTER);
            frame.add(createControlPanel(), BorderLayout.SOUTH);

            // Display the window
            frame.setVisible(true);

            // Prompt the user to select points
            JOptionPane.showMessageDialog(frame,
                    "Please select two points on the map:\n" +
                            "1. Left-click to set the start point.\n" +
                            "2. Right-click to set the end point.",
                    "Instructions", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private static Graph createGraph() {
        Graph graph = new Graph();

        Node nodeA = new Node("1", 37.7749, -122.4194);
        Node nodeB = new Node("2", 10.873767739353797, 39.17724609375);
        Node nodeC = new Node("3", 11.14606637590688, 39.638671875);

        graph.addNode(nodeA);
        graph.addNode(nodeB);
        graph.addNode(nodeC);

        graph.addEdge(nodeA, nodeB, distance(nodeA, nodeB));
        graph.addEdge(nodeB, nodeC, distance(nodeB, nodeC));
        graph.addEdge(nodeA, nodeC, distance(nodeA, nodeC));
        System.out.println("The graph part works");
        System.out.println(graph);
        return graph;
    }

    private static double distance(Node n1, Node n2) {
        double latDiff = n1.getLat() - n2.getLat();
        double lonDiff = n1.getLon() - n2.getLon();
        return Math.sqrt(latDiff * latDiff + lonDiff * lonDiff);
    }

    private static JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Left-click: set start | Right-click: set end"));
        return panel;
    }

}