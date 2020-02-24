package io.appgal.cloud.geospatial;

import org.geotools.data.DataUtilities;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDriveTests {
    private static Logger logger = LoggerFactory.getLogger(TestDriveTests.class);

    @Test
    public void testSimpleFeatureType() throws Exception
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

    @Test
    public void testSimpleFeatureCreation() throws Exception
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

        /*
         * A list to collect features as we create them.
         */
        List<SimpleFeature> features = new ArrayList<>();

        /*
         * GeometryFactory will be used to create the geometry attribute of each feature,
         * using a Point object for the location.
         */
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            /* First line of the data file is the header */
            String line = reader.readLine();
            System.out.println("Header: " + line);

            for (line = reader.readLine(); line != null; line = reader.readLine()) {
                if (line.trim().length() > 0) { // skip blank lines
                    String tokens[] = line.split("\\,");

                    double latitude = Double.parseDouble(tokens[0]);
                    double longitude = Double.parseDouble(tokens[1]);
                    String name = tokens[2].trim();
                    int number = Integer.parseInt(tokens[3].trim());

                    /* Longitude (= x coord) first ! */
                    Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));

                    featureBuilder.add(point);
                    featureBuilder.add(name);
                    featureBuilder.add(number);
                    SimpleFeature feature = featureBuilder.buildFeature(null);
                    features.add(feature);
                }
            }
        }
    }

    @Test
    public void testSimpleFeaturePersistence() throws Exception
    {
        final SimpleFeatureType TYPE =
                DataUtilities.createType(
                        "Location",
                        "the_geom:Point:srid=4326,"
                                + // <- the geometry attribute: Point type
                                "name:String,"
                                + // <- a String attribute
                                "number:Integer" // a number attribute
                );

        /*
         * Get an output file name and create the new shapefile
         */
        File newFile = new File("simpleFeaturePersistence.shp");

        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

        Map<String, Serializable> params = new HashMap<>();
        params.put("url", newFile.toURI().toURL());
        params.put("create spatial index", Boolean.TRUE);

        ShapefileDataStore newDataStore =
                (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);

        /*
         * TYPE is used as a template to describe the file contents
         */
        newDataStore.createSchema(TYPE);
    }
}
