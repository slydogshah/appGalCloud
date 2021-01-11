package io.appgal.cloud;

import com.google.gson.JsonObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

import com.google.gson.JsonParser;

import io.bugsbunny.data.history.service.DataReplayService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Random;

import io.appgal.cloud.model.*;
import io.appgal.cloud.model.MessageWindow;

@Path("/microservice")
public class Microservice {
    private static Logger logger = LoggerFactory.getLogger(Microservice.class);

    @Inject
    private DataReplayService dataReplayService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String hello()
    {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("oid", UUID.randomUUID().toString());
        jsonObject.addProperty("message", "HELLO_TO_HUMANITY");

        return jsonObject.toString();
    }
}