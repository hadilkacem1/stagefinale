package com.Telnet.AuthService.service;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AuthService {

    private final CloseableHttpClient httpClient;

    @Autowired
    public AuthService(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String makeHttpRequest() {
        try {
            HttpGet request = new HttpGet("http://auth-service:8805/actuator/health");
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                return EntityUtils.toString(response.getEntity());
            }
        } catch (IOException e) {
            // Log the exception or handle it appropriately
            e.printStackTrace();
            return "Error occurred while making HTTP request to auth-service: " + e.getMessage();
        }
    }}
