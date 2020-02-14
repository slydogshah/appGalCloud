package io.appgal.cloud.dataLake;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class DeltaLakeTests {
    private static Logger logger = LoggerFactory.getLogger(DeltaLakeTests.class);

    @Test
    public void testHelloSpark()
    {
        SparkSession spark = SparkSession.builder().getOrCreate();

        Dataset<Long> data = spark.range(0, 5);
        data.write().format("delta").save("/tmp/delta-table");
    }
}
