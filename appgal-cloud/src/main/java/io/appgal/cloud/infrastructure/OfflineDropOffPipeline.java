package io.appgal.cloud.infrastructure;

import com.google.gson.JsonArray;
import io.appgal.cloud.network.services.DropOffPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class OfflineDropOffPipeline {
    private static Logger logger = LoggerFactory.getLogger(OfflineDropOffPipeline.class);

    @Inject
    private DropOffPipeline dropOffPipeline;

    public JsonArray findRunnersWithDynamicDropOff()
    {
        return new JsonArray();
    }
}
