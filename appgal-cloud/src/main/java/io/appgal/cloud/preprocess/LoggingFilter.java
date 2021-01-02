package io.appgal.cloud.preprocess;

import io.vertx.core.http.HttpServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

//@Provider
public class LoggingFilter implements ContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingFilter.class);

    @Context
    UriInfo info;

    @Context
    HttpServerRequest request;

    @Override
    public void filter(ContainerRequestContext context) {
        LOG.info("*******USER-AGENT******************");
        LOG.info("User-Agent: "+request.getHeader("User-Agent"));
        LOG.info("***********************************");
    }
}
