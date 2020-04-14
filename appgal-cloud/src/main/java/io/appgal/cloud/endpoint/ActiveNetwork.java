package io.appgal.cloud.endpoint;

import com.google.gson.JsonObject;
import io.appgal.cloud.network.NetworkOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("activeNetwork")
public class ActiveNetwork {
    private static Logger logger = LoggerFactory.getLogger(ActiveNetwork.class);

    @Inject
    private NetworkOrchestrator networkOrchestrator;

    @Path("activeView")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getActiveView()
    {
        JsonObject activeView = this.networkOrchestrator.getActiveView();
        return activeView.toString();
    }
}
