package com.pathfinder;

import javafx.scene.web.WebEngine;

import java.util.List;

public class MapController {
    private final WebEngine webEngine;

    public MapController(WebEngine engine) {
        this.webEngine = engine;
    }

    public void drawRoute(List<double[]> coordinates) {

        StringBuilder jsArray = new StringBuilder("[");
        for (double[] coord : coordinates) {
            jsArray.append("[").append(coord[0]).append(",").append(coord[1]).append("],");
        }
        if (coordinates.size() > 0)
            jsArray.setLength(jsArray.length() - 1); // remove last comma
        jsArray.append("]");
        webEngine.executeScript("drawRoute(" + jsArray + ");");
    }

}
