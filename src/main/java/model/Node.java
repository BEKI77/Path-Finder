package model;

import org.openstreetmap.gui.jmapviewer.Coordinate;

public class Node {
    private final String id;
    private final Coordinate coordinate;

    public Node(String id, double lat, double lon) {
        this.id = id;
        this.coordinate = new Coordinate(lat, lon);
    }

    public String getId() {
        return this.id;
    }

    public Coordinate getCoordinate() {
        return this.coordinate;
    }

    public double getLat() {
        return this.coordinate.getLat();
    }

    public double getLon() {
        return this.coordinate.getLon();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Node node = (Node) o;
        return id.equals(node.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}