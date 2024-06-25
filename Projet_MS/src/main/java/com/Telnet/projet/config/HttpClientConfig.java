package com.Telnet.projet.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfig {

    @Bean
    public CloseableHttpClient httpClient() {
        int timeout = 30; // en secondes

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeout * 1000) // Timeout de connexion en millisecondes
                .setSocketTimeout(timeout * 1000) // Timeout de lecture des données en millisecondes
                .build();

        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .build();
    }
}
