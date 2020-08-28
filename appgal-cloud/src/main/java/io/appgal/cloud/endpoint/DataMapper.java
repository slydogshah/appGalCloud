package io.appgal.cloud.endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("dataMapper")
public class DataMapper {
    private static Logger logger = LoggerFactory.getLogger(DataMapper.class);

    @Path("map")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response map()
    {
        Response response = Response.ok().build();
        return response;
    }
}