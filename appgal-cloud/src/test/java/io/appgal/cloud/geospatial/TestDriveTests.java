package io.appgal.cloud.geospatial;

import org.geotools.data.DataUtilities;
import org.junit.jupiter.api.Test;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;

public class TestDriveTests {
    private static Logger logger = LoggerFactory.getLogger(TestDriveTests.class);

    @Test
    public void testTestDrive() throws Exception
    {
        // Set cross-platform look & feel for compatability
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

        String location = Thread.currentThread().getContextClassLoader().getResource("location.csv").getFile();
        File file = new File(location);

        final SimpleFeatureType TYPE =
                DataUtilities.createType(
                        "Location",
                        "the_geom:Point:srid=4326,"
                                + // <- the geometry attribute: Point type
                                "name:String,"
                                + // <- a String attribute
                                "number:Integer" // a number attribute
                );
        logger.info("TYPE:" + TYPE);
    }
}
