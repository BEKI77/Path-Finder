package com.pathfinder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
// import javafx.concurrent.Worker;
import javafx.beans.value.ChangeListener;
// import javafx.beans.value.ObservableValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class Main extends Application {
    public JSObject window;
    public JavaConnector tmp;

    @Override
    public void start(Stage primaryStage) {
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();
        engine.setJavaScriptEnabled(true);

        engine.load(getClass().getResource("/map2.html").toExternalForm());

        engine.setConfirmHandler(null);

        engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue,
                    Worker.State newValue) {
                if (newValue == Worker.State.SUCCEEDED) {
                    System.out.println("Change happend");
                    initializeJavaBridge(engine);
                    loadGeoJson(engine);
                }
            }
        });

        Scene scene = new Scene(webView, 1000, 800);
        primaryStage.setTitle("Path Finder");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeJavaBridge(WebEngine engine) {
        this.window = (JSObject) engine.executeScript("window");
        this.tmp = new JavaConnector();
        this.window.setMember("javaConnector", this.tmp);
        System.out.println("JavaConnector mounted");
    }

    private void loadGeoJson(WebEngine engine) {
        try {
            // Load the GeoJSON file as a string
            String geoJson = new String(Files.readAllBytes(
                    Paths.get("/home/bek/follow-the-light/P-J/DSA-proj/src/main/resources/AASTU.geojson")));

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode geoJsonObject = (ObjectNode) mapper.readTree(geoJson);

            ArrayNode features = (ArrayNode) geoJsonObject.get("features");
            ArrayNode filteredFeatures = mapper.createArrayNode();

            for (int i = 0; i < features.size(); i++) {
                ObjectNode feature = (ObjectNode) features.get(i);

                // Example filter: Keep only points with a specific property value
                if (feature.get("geometry").get("type").asText().equals("Point")) {
                    filteredFeatures.add(feature);
                }
            }

            geoJsonObject.set("features", filteredFeatures);

            String filteredGeoJson = mapper.writeValueAsString(geoJsonObject);

            // System.out.println("Filtered GeoJSON: " + filteredGeoJson);
            // Pass the GeoJSON data to JavaScript
            engine.executeScript("loadGeoJson(" + filteredGeoJson + ");");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
