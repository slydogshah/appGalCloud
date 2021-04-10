package io.appgal.cloud.infrastructure;

import javax.inject.Singleton;
import java.net.http.HttpClient;

@Singleton
public class Http {

    private static final String URL = "http://maps.googleapis.com/maps/api/geocode/json";

    private HttpClient httpClient = HttpClient.newBuilder().build();

    public HttpClient getHttpClient() {
        return httpClient;
    }
}
