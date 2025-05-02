package com.pathfinder;

import com.pathfinder.model.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import javafx.beans.value.ChangeListener;
import com.fasterxml.jackson.databind.node.*;
import com.fasterxml.jackson.databind.*;

public class Main extends Application {
    public WebView webView;
    public WebEngine engine;
    public String geoJson;
    public JSObject window;
    public JavaConnector tmp;
    public ArrayNode features;
    Graph graph;

    @Override
    public void start(Stage primaryStage) {
        this.graph = new Graph();

        this.webView = new WebView();
        this.engine = this.webView.getEngine();

        this.engine.setJavaScriptEnabled(true);

        this.engine.load(getClass().getResource("/map2.html").toExternalForm());

        this.engine.setConfirmHandler(null);

        initializeGraph();

        this.engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue,
                    Worker.State newValue) {
                if (newValue == Worker.State.SUCCEEDED) {
                    System.out.println("Change happend");
                    initializeJavaBridge(engine);
                }
            }
        });

        Scene scene = new Scene(webView, 1000, 1000);
        primaryStage.setTitle("Path Finder");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeJavaBridge(WebEngine engine) {
        this.window = (JSObject) engine.executeScript("window");
        this.tmp = new JavaConnector();
        this.window.setMember("javaConnector", this.tmp);
        this.window.setMember("javaGraph", this.graph);
        System.out.println("JavaConnector mounted");
    }

    private void initializeGraph() {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/AASTU.geojson");

            if (inputStream == null) {
                System.err.println("GeoJSON file not found!");
                return;
            }

            this.geoJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode geoJsonObject = (ObjectNode) mapper.readTree(this.geoJson);

            this.features = (ArrayNode) geoJsonObject.get("features");

            this.graph.buildFromGeoJson(this.features);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // private void loadGeoJson(ArrayNode data) {
    // try {
    // if (this.features == null) {
    // System.err.println("GeoJSON file not found!");
    // return;
    // }

    // ObjectMapper mapper = new ObjectMapper();
    // ObjectNode geoJsonObject = (ObjectNode) mapper.readTree(this.geoJson);
    // geoJsonObject.set("features", data);

    // String dataSentToBeDisplayed = mapper.writeValueAsString(geoJsonObject);

    // engine.executeScript("loadGeoJson(" + dataSentToBeDisplayed + ");");

    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }

    public static void main(String[] args) {
        launch(args);
    }
}
