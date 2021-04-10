package jen;

import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/microservice")
public class Microservice {
    private static Logger logger = LoggerFactory.getLogger(Microservice.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response hello()
    {
        try {
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("product", "#Jen Network");
            jsonObject.addProperty("oid", UUID.randomUUID().toString());
            jsonObject.addProperty("message", "HELLO_TO_HUMANITY");

            return Response.ok(jsonObject.toString()).build();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return Response.status(500).build();
        }
    }
}