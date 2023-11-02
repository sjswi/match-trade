package com.flying.cattle.dapr.plugin.dapr;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import java.net.URL;

/**
 * @program: match-trade
 * @description:
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2023-11-01 20:12
 **/

public class DaprClient {
    private static final String sqlBindingName = "dapr-mysql";
    private static final String DAPR_HOST = System.getenv().getOrDefault("DAPR_HOST", "http://localhost");
    private static final String DAPR_HTTP_PORT = System.getenv().getOrDefault("DAPR_HTTP_PORT", "3503");

    private static final String stateBindingName = "dapr-mysql";
    public DaprClient(){

    }

    public void exec(String sql) throws IOException, InterruptedException {
        JSONObject command = new JSONObject();
        command.put("sql", sql);

        JSONObject payload = new JSONObject();
        payload.put("metadata", command);
        payload.put("operation", "exec");
        String daprUri = DAPR_HOST + ":" + DAPR_HTTP_PORT + "/v1.0/bindings/" + sqlBindingName;
        URL url = new URL(daprUri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set up HTTP POST
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Send the POST data
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = payload.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Read the response
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return;
            }
        } else {
            // Handle error response here if needed
            throw new RuntimeException("Failed : HTTP error code : " + responseCode);
        }
    }

    public String query(String sql) throws IOException, InterruptedException {
        JSONObject command = new JSONObject();
        command.put("sql", sql);

        JSONObject payload = new JSONObject();
        payload.put("metadata", command);
        payload.put("operation", "query");
        String daprUri = DAPR_HOST + ":" + DAPR_HTTP_PORT + "/v1.0/bindings/" + sqlBindingName;
        URL url = new URL(daprUri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set up HTTP POST
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Send the POST data
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = payload.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Read the response
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            }
        } else {
            // Handle error response here if needed
            throw new RuntimeException("Failed : HTTP error code : " + responseCode);
        }
    }


    public String get(String key) throws IOException, InterruptedException {
        JSONObject command = new JSONObject();
//        command.put("sql", sql);

        JSONObject payload = new JSONObject();
        payload.put("metadata", command);
        payload.put("operation", "query");
        String daprUri = DAPR_HOST + ":" + DAPR_HTTP_PORT + "/v1.0/bindings/" + sqlBindingName;
        URL url = new URL(daprUri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set up HTTP POST
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Send the POST data
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = payload.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Read the response
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            }
        } else {
            // Handle error response here if needed
            throw new RuntimeException("Failed : HTTP error code : " + responseCode);
        }
    }
}
