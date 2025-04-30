package com.pathfinder;

import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

import java.util.List;

public class MapController {
    private final WebEngine webEngine;

    public MapController(WebEngine engine) {
        this.webEngine = engine;

        webEngine.documentProperty().addListener((obs, oldDoc, newDoc) -> {
            if (newDoc != null) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaConnector", new JavaConnector());
            }
        });
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
