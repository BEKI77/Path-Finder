package com.pathfinder;

import com.gluonhq.maps.*;
import com.pathfinder.model.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;

import com.fasterxml.jackson.databind.node.*;
import com.fasterxml.jackson.databind.*;

public class Main extends Application {
    private List<MapLayer> layers = new ArrayList<>();

    public MapView mapView;
    public String geoJson;
    public ArrayNode features;
    public Point startCoord;
    public Point endCoord;
    Graph graph;

    @Override
    public void start(Stage primaryStage) {
        this.graph = new Graph();
        initializeGraph();

        this.mapView = new MapView();
        this.mapView.setZoom(16);
        this.mapView.setCenter(new MapPoint(8.888655, 38.812035));

        Label startLabel = new Label("Start: not selected");
        Label endLabel = new Label("End: not selected");
        Label distance = new Label("Distance in Km: 0");

        startLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333; -fx-font-weight: bold;");
        endLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333; -fx-font-weight: bold;");

        VBox infoBox = new VBox(5, startLabel, endLabel, distance);

        infoBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9);" +
                "-fx-padding: 12;" +
                "-fx-border-color:rgb(0, 61, 102);" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0.5, 0, 2);");
        infoBox.setMaxWidth(200);
        infoBox.setMaxHeight(80);

        this.mapView.setOnMouseClicked((event -> {

            if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {

                Point2D screenPoint = new Point2D(event.getX(), event.getY());
                MapPoint mapPoint = mapView.getMapPosition(screenPoint.getX(), screenPoint.getY());

                if (this.startCoord == null) {

                    for (MapLayer layer : this.layers) {
                        this.mapView.removeLayer(layer);
                    }

                    this.startCoord = new Point(mapPoint.getLatitude(), mapPoint.getLongitude());

                    startLabel.setText(String.format("Start: %.5f, %.5f", this.startCoord.lat, this.startCoord.lon));

                    Point startNearest = this.graph.findNearestNode(this.startCoord);
                    MarkerRed customLayer1 = new MarkerRed(new MapPoint(startNearest.lat, startNearest.lon),
                            Color.GREEN);

                    this.layers.add(customLayer1);
                    this.mapView.addLayer(customLayer1);
                    customLayer1.layoutLayer();

                } else {

                    this.endCoord = new Point(mapPoint.getLatitude(), mapPoint.getLongitude());

                    endLabel.setText(String.format("End: %.5f, %.5f", this.endCoord.lat, this.endCoord.lon));

                    Point startNearest = this.graph.findNearestNode(this.startCoord);
                    Point endNearest = this.graph.findNearestNode(this.endCoord);

                    MarkerRed customLayer2 = new MarkerRed(new MapPoint(endNearest.lat, endNearest.lon),
                            Color.DARKGREEN);

                    this.layers.add(customLayer2);
                    this.mapView.addLayer(customLayer2);
                    customLayer2.layoutLayer();

                    List<List<Point>> result = this.graph.findAllPaths(startNearest.lon,
                            startNearest.lat,
                            endNearest.lon,
                            endNearest.lat);

                    distance.setText(String.format("Distance in Km: %.5f", this.graph.totDistance));

                    updateRoadLayer(result);

                    startCoord = null;
                    endCoord = null;
                }

            }
        }));

        StackPane root = new StackPane();
        root.getChildren().addAll(mapView, infoBox);

        StackPane.setAlignment(infoBox, javafx.geometry.Pos.BOTTOM_RIGHT);

        Scene scene = new Scene(root, 1200, 1000);
        primaryStage.setTitle("AASTU Path Finder");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateRoadLayer(List<List<Point>> allPath) {
        for (List<Point> coords : allPath) {
            List<MapPoint> pathPoints = new ArrayList<>();

            for (Point coord : coords) {
                double lon = coord.lon;
                double lat = coord.lat;
                pathPoints.add(new MapPoint(lat, lon));
            }

            PathLayer roadLayer = new PathLayer(pathPoints);
            this.layers.add(roadLayer);
            this.mapView.addLayer(roadLayer);
            roadLayer.layoutLayer();
        }
    }

    private void initializeGraph() {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/MapData.geojson");

            if (inputStream == null) {
                System.err.println("GeoJSON file not found!");
                return;
            }

            this.geoJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode geoJsonObject = (ObjectNode) mapper.readTree(this.geoJson);

            this.features = (ArrayNode) geoJsonObject.get("features");

            this.graph.buildFromGeoJson(this.features);
            System.out.println("The Graph: " + this.graph);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public class MarkerRed extends MapLayer {
        private MapPoint point;
        private Color color = Color.GREEN;

        public MarkerRed(MapPoint coord, Color col) {
            this.color = col;
            this.point = coord;
            System.out.println(this.point.getLatitude() + " " + this.point.getLongitude());
        }

        @Override
        protected void layoutLayer() {
            getChildren().clear();
            Node icon = new Circle(5, color);
            Point2D mapPoint = getMapPoint(point.getLatitude(), point.getLongitude());
            icon.setVisible(true);
            icon.setTranslateX(mapPoint.getX());
            icon.setTranslateY(mapPoint.getY());

            getChildren().add(icon);
        }
    }

    public class PathLayer extends MapLayer {
        private List<MapPoint> pathPoints;

        public PathLayer(List<MapPoint> pathCoords) {
            this.pathPoints = pathCoords;
        }

        @Override
        protected void layoutLayer() {
            getChildren().clear();

            Path path = new Path();
            path.setVisible(true);
            path.setStroke(Color.BLUE);
            path.setStrokeWidth(2);
            boolean first = true;

            for (MapPoint coord : pathPoints) {
                Point2D point = getMapPoint(coord.getLatitude(), coord.getLongitude());
                if (first) {
                    path.getElements().add(new MoveTo(point.getX(), point.getY()));
                    first = false;
                } else {
                    path.getElements().add(new LineTo(point.getX(), point.getY()));
                }
            }
            getChildren().add(path);
        }
    }
}
