package com.pathfinder;

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

    public void sendCoordinates(String json) {
        System.out.println("Received coordinates: " + json);
        // Parse and use as needed
    }

    public int getCount() {
        return count;
    }
}
