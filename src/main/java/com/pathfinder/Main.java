package com.pathfinder;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();

        engine.load(getClass().getResource("/map.html").toExternalForm());

        JSObject window = (JSObject) engine.executeScript("window");

        window.setMember("javaConnector", new JavaConnector());
        System.out.println(window);
        // engine.documentProperty().addListener((obs, oldDoc, newDoc) -> {
        // if (newDoc != null) {
        // engine.executeScript("setupClickListener();");
        // }
        // });

        Scene scene = new Scene(webView, 800, 600);
        primaryStage.setTitle("Path Finder");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
