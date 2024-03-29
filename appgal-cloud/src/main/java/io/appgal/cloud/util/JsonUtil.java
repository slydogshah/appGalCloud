package io.appgal.cloud.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtil {
    private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void print(Class caller,JsonElement jsonElement)
    {
        logger.info("*****JSONUtil*********************");
        logger.info("CALLER: "+caller.toString());
        if(jsonElement.isJsonArray())
        {
            logger.info("******ARRAY_SIZE: "+jsonElement.getAsJsonArray().size()+"**********");
        }
        logger.info(gson.toJson(jsonElement));
    }
}
