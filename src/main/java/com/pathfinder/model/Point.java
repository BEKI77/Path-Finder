package com.pathfinder.model;

import java.util.*;
import com.fasterxml.jackson.annotation.JsonProperty;

class Point {
    @JsonProperty
    public double lat;

    @JsonProperty
    public double lon;

    Point(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point))
            return false;
        Point p = (Point) o;
        return Double.compare(lat, p.lat) == 0 && Double.compare(lon, p.lon) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lon);
    }
}
