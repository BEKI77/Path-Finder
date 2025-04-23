package com.pathfinder;


import javafx.application.Platform;
import javafx.scene.web.WebEngine;

public class JavaConnector {
    private final WebEngine webEngine;

    public JavaConnector(WebEngine webEngine) {
        this.webEngine = webEngine;
    }

    public void onMapClick(double lat1, double lng1, double lat2, double lng2) {
        new Thread(() -> {
            String polyline = GraphHopperClient.getRoute(lat1, lng1, lat2, lng2);
            Platform.runLater(() -> {
                webEngine.executeScript("drawRoute(" + polyline + ")");
            });
        }).start();
    }
}
