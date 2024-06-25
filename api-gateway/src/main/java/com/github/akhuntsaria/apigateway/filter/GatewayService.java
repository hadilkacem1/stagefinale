package com.github.akhuntsaria.apigateway.filter;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class GatewayService {

    private final CloseableHttpClient httpClient;

    @Autowired
    public GatewayService(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String makeHttpRequest() throws IOException {
        // Configure HTTP client to use a custom DNS resolver
        CloseableHttpClient customHttpClient = HttpClients.custom()
                .useSystemProperties()
                .build();

        // Example: making a GET request to a downstream service
        HttpGet request = new HttpGet("http://auth-service/actuator/health");

        try (CloseableHttpResponse response = customHttpClient.execute(request)) {
            // Process the response
            return EntityUtils.toString(response.getEntity());
        }
    }
}
