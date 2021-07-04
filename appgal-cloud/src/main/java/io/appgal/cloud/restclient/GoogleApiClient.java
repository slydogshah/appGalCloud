package io.appgal.cloud.restclient;

import io.appgal.cloud.infrastructure.Http;
import io.appgal.cloud.model.Address;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.model.Location;
import io.appgal.cloud.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class GoogleApiClient {
    private static Logger logger = LoggerFactory.getLogger(GoogleApiClient.class);

    @Inject
    private Http http;


    public JsonObject convertAddressToCoordinates(Address address)
    {
        try {
            JsonObject result;

            String addressParam = address.getStreet()+","+address.getZip();
            addressParam = URLEncoder.encode(addressParam, StandardCharsets.UTF_8);

            //Create the Experiment
            //TODO
            HttpClient httpClient = http.getHttpClient();
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address="+addressParam+"&key=AIzaSyAgTAjlBc6KHvCARz6n6CCCt_Uob6-JB2I";

            HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder();
            HttpRequest httpRequest = httpRequestBuilder.uri(new URI(url))
                    .GET()
                    .build();


            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            String responseJson = httpResponse.body();
            int status = httpResponse.statusCode();
            if (status != 200) {
                throw new RuntimeException("GEOCODE_API_UNAVAILABLE");
            }

            JsonObject original = JsonParser.parseString(responseJson).getAsJsonObject();
            JsonArray results = original.get("results").getAsJsonArray();
            result = results.get(0).getAsJsonObject().get("geometry").getAsJsonObject();
            return result;
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }


    public JsonObject estimateTime(Location start, Location end)
    {
        try {
            JsonObject result;

            //JsonUtil.print(this.getClass(),start.toJson());
            //JsonUtil.print(this.getClass(),end.toJson());

            String origins = start.getLatitude() + "," + start.getLongitude();
            String destinations = end.getLatitude() + "," + end.getLongitude();

            //Create the Experiment
            //TODO
            HttpClient httpClient = http.getHttpClient();
            String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins="+origins+"" +
                    "&destinations="+destinations+"&key=AIzaSyAgTAjlBc6KHvCARz6n6CCCt_Uob6-JB2I";

            HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder();
            HttpRequest httpRequest = httpRequestBuilder.uri(new URI(url))
                    .GET()
                    .build();


            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            String responseJson = httpResponse.body();
            int status = httpResponse.statusCode();
            if (status != 200) {
                throw new RuntimeException("TIME_ESTIMATES_NOT_AVAILABLE");
            }

            JsonObject original = JsonParser.parseString(responseJson).getAsJsonObject();

            JsonArray rows = original.get("rows").getAsJsonArray();
            JsonArray elements = rows.get(0).getAsJsonObject().get("elements").getAsJsonArray();
            JsonObject data = elements.get(0).getAsJsonObject();
            JsonObject duration = data.get("duration").getAsJsonObject();


            result = new JsonObject();
            result.addProperty("duration",duration.get("text").getAsString());
            return result;
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }

}
