package io.appgal.cloud.endpoint;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appgal.cloud.customer.CustomerService;
import io.appgal.cloud.model.SourceOrg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("customer")
public class Customer {
    private static Logger logger = LoggerFactory.getLogger(Customer.class);

    @Inject
    private CustomerService customerService;

    @Path("get")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getCustomer(@QueryParam("orgId") String orgId)
    {
        SourceOrg sourceOrg = this.customerService.getSourceOrg(orgId);
        return sourceOrg.toString();
    }

    @Path("save")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String saveCustomer(@RequestBody String customerJson)
    {
        logger.info("*******");
        logger.info(customerJson);
        logger.info("*******");

        JsonObject jsonObject = JsonParser.parseString(customerJson).getAsJsonObject();
        SourceOrg sourceOrg = SourceOrg.parseJson(jsonObject);
        this.customerService.storeSourceOrg(sourceOrg);

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("statusCode", "0");
        return responseJson.toString();
    }
}