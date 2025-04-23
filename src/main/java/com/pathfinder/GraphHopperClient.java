package com.pathfinder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public class GraphHopperClient {
    public static String getRoute(double lat1, double lon1, double lat2, double lon2) {
        try {
            String urlStr = String.format(
                    "http://localhost:8989/route?point=%f,%f&point=%f,%f&type=json&points_encoded=false",
                    lat1, lon1, lat2, lon2);

            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = reader.lines().collect(Collectors.joining());
            reader.close();

            JsonNode json = new ObjectMapper().readTree(response);
            JsonNode points = json.path("paths").get(0).path("points").path("coordinates");

            StringBuilder jsArray = new StringBuilder("[");
            for (JsonNode point : points) {
                double lon = point.get(0).asDouble();
                double lat = point.get(1).asDouble();
                jsArray.append(String.format("{lat: %f, lng: %f},", lat, lon));
            }
            if (jsArray.charAt(jsArray.length() - 1) == ',') {
                jsArray.setLength(jsArray.length() - 1); // remove trailing comma
            }
            jsArray.append("]");
            return jsArray.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }
}
