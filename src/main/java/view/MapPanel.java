package view;

import org.openstreetmap.gui.jmapviewer.*;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import model.Node;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.BorderLayout;
import javax.swing.JPanel;
// import java.awt.event.MouseWheelEvent;
import javax.swing.JLabel;
import java.util.List;

public class MapPanel extends JPanel implements JMapViewerEventListener {
    private static final long serialVersionUID = 1L;

    private JMapViewerTree treeMap;
    private JLabel zoomLabel;
    private JLabel zoomValue;
    private JLabel mperpLabelName;
    private JLabel mperpLabelValue;

    private static final Color PATH_COLOR = new Color(0, 100, 255, 180);
    private static final Color START_COLOR = new Color(0, 200, 0, 200);
    private static final Color END_COLOR = new Color(200, 0, 0, 200);
    private static final Color NODE_COLOR = new Color(100, 100, 100, 150);

    // Ethiopia's bounding coordinates
    private static final double ETHIOPIA_MIN_LAT = 3.4;
    private static final double ETHIOPIA_MAX_LAT = 14.9;
    private static final double ETHIOPIA_MIN_LON = 33.0;
    private static final double ETHIOPIA_MAX_LON = 48.0;
    private static final Coordinate ETHIOPIA_CENTER = new Coordinate(9.1, 40.5);
    private static final int ETHIOPIA_ZOOM_LEVEL = 6;

    /**
     * Setups the JFrame layout, sets some default options for the JMapViewerTree
     * and displays a map in the window.
     */
    public MapPanel() {
        super();
        setLayout(new BorderLayout()); // Set the layout for the panel
        treeMap = new JMapViewerTree("Zones");
        setupPanels();

        // Listen to the map viewer for user operations
        map().addJMVListener(this);
        setDisplayPosition(ETHIOPIA_CENTER, ETHIOPIA_ZOOM_LEVEL);
        // Set some options for the map viewer
        map().setTileSource(new OsmTileSource.Mapnik());
        map().setTileLoader(new OsmTileLoader(map()));
        map().setMapMarkerVisible(true);
        map().setZoomControlsVisible(true);

        // Activate map in the panel
        treeMap.setTreeVisible(true);
        add(treeMap, BorderLayout.CENTER);
    }

    private JMapViewer map() {
        return treeMap.getViewer();
    }

    private void removeAllMapMarkers() {
        map().removeAllMapMarkers();
    }

    /**
     * Sets the display position of the map to the given coordinate and zoom level.
     * 
     * @param coordinate The coordinate to center the map on.
     * @param zoom       The zoom level to set.
     */
    private void setDisplayPosition(Coordinate coordinate, int zoom) {
        map().setDisplayPosition(coordinate, zoom);
    }

    private void removeAllMapPolygons() {
        map().removeAllMapPolygons();
    }

    /**
     * Adds a map polygon to the map viewer.
     * 
     * @param polygon The polygon to add.
     */
    private void addMapPolygon(MapPolygon polygon) {
        map().addMapPolygon(polygon);
    }

    /**
     * Sets up additional panels and components for the JFrame.
     */
    private void setupPanels() {
        zoomLabel = new JLabel("Zoom: ");
        zoomValue = new JLabel(String.valueOf(map().getZoom()));
        mperpLabelName = new JLabel("Meters/Pixels: ");
        mperpLabelValue = new JLabel(String.valueOf(map().getMeterPerPixel()));

        // Add these components to the JFrame or a specific panel as needed
        // Example:
        JPanel panel = new JPanel();
        panel.add(zoomLabel);
        panel.add(zoomValue);
        panel.add(mperpLabelName);
        panel.add(mperpLabelValue);
        add(panel, BorderLayout.NORTH);
    }

    /**
     * @param args Main program arguments
     */

    @Override
    public void processCommand(JMVCommandEvent command) {
        // ...
    }

    // public MapPanel() {
    // super();
    //

    // }

    public Coordinate getPosition(int x, int y) {
        return (Coordinate) map().getPosition(x, y);
    }

    public void displayPaths(List<List<Node>> paths) {
        this.removeAllMapMarkers();
        this.removeAllMapPolygons();

        if (paths == null || paths.isEmpty()) {
            // Reset to Ethiopia view when no paths
            setDisplayPosition(ETHIOPIA_CENTER, ETHIOPIA_ZOOM_LEVEL);
            return;
        }

        // Rest of your existing displayPaths method...
        Color[] pathColors = { PATH_COLOR, new Color(255, 100, 0, 180), new Color(100, 255, 0, 180) };
        int colorIndex = 0;

        for (List<Node> path : paths) {
            if (path == null || path.isEmpty())
                continue;

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
                map().addMapMarker(marker);
            }

            Color currentPathColor = pathColors[colorIndex % pathColors.length];

            for (int i = 1; i < path.size(); i++) {
                Node from = path.get(i - 1);
                Node to = path.get(i);

                MapPolygon line = new MapPolygonImpl(from.getCoordinate(), to.getCoordinate());
                line.getStyle().setColor(currentPathColor);
                line.getStyle().setStroke(new BasicStroke(4));
                this.addMapPolygon(line);
            }

            colorIndex++;

        }

    }

}
