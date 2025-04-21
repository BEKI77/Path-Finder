package view;

import org.openstreetmap.gui.jmapviewer.*;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;
import model.Node;
import java.awt.BasicStroke;
import java.awt.Color;
import java.util.List;

public class MapPanel extends JMapViewer {
    private static final Color PATH_COLOR = new Color(0, 100, 255, 180);
    private static final Color START_COLOR = new Color(0, 200, 0, 200);
    private static final Color END_COLOR = new Color(200, 0, 0, 200);
    private static final Color NODE_COLOR = new Color(100, 100, 100, 150);

    public void displayPaths(List<List<Node>> paths) {
        this.removeAllMapMarkers();
        this.removeAllMapPolygons();

        if (paths == null || paths.isEmpty())
            return;

        // Define colors for multiple paths
        Color[] pathColors = { PATH_COLOR, new Color(255, 100, 0, 180), new Color(100, 255, 0, 180) };
        int colorIndex = 0;

        for (List<Node> path : paths) {
            if (path == null || path.isEmpty())
                continue;

            // Add markers for all nodes in the current path
            for (int i = 0; i < path.size(); i++) {
                Node node = path.get(i);
                MapMarker marker;

                if (i == 0) {
                    marker = new MapMarkerDot(node.getCoordinate());
                    marker.getStyle().setColor(START_COLOR);
                } else if (i == path.size() - 1) {
                    marker = new MapMarkerDot(node.getCoordinate());
                    marker.getStyle().setColor(END_COLOR);
                } else {
                    marker = new MapMarkerDot(node.getCoordinate());
                    marker.getStyle().setColor(NODE_COLOR);
                }

                marker.getStyle().setBackColor(Color.WHITE);
                this.addMapMarker(marker);
            }

            // Draw path lines for the current path
            Color currentPathColor = pathColors[colorIndex % pathColors.length];
            for (int i = 1; i < path.size(); i++) {
                Node from = path.get(i - 1);
                Node to = path.get(i);

                MapPolygon line = new MapPolygonImpl(
                        from.getCoordinate(),
                        to.getCoordinate());
                line.getStyle().setColor(currentPathColor);
                line.getStyle().setStroke(new BasicStroke(4));
                this.addMapPolygon(line);
            }

            colorIndex++;
        }

        // Zoom to fit all paths
        zoomToPaths(paths);
    }

    private void zoomToPaths(List<List<Node>> paths) {
        if (paths.isEmpty())
            return;

        double minLat = Double.MAX_VALUE;
        double maxLat = Double.MIN_VALUE;
        double minLon = Double.MAX_VALUE;
        double maxLon = Double.MIN_VALUE;

        for (List<Node> path : paths) {
            for (Node node : path) {
                minLat = Math.min(minLat, node.getLat());
                maxLat = Math.max(maxLat, node.getLat());
                minLon = Math.min(minLon, node.getLon());
                maxLon = Math.max(maxLon, node.getLon());
            }
        }

        // Add some padding
        double latPadding = (maxLat - minLat) * 0.2;
        double lonPadding = (maxLon - minLon) * 0.2;

        double centerLat = (minLat + maxLat) / 2;
        double centerLon = (minLon + maxLon) / 2;

        int zoomLevel = getBestZoomLevel(minLat - latPadding, maxLat + latPadding, minLon - lonPadding,
                maxLon + lonPadding);

        this.setDisplayPosition(new Coordinate(centerLat, centerLon), zoomLevel);
    }

    private int getBestZoomLevel(double minLat, double maxLat, double minLon, double maxLon) {
        // Calculate an appropriate zoom level based on the bounding box
        // This is a placeholder; you may need to implement logic based on your map
        // viewer's API
        return 10; // Example zoom level
    }

}