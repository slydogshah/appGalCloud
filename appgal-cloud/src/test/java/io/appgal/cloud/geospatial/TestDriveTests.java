package io.appgal.cloud.geospatial;

import org.geotools.data.*;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.geotools.referencing.crs.DefaultGeocentricCRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.referencing.cs.DefaultEllipsoidalCS;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.action.SafeAction;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.spatial4j.distance.DistanceUtils;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class TestDriveTests {
    private static Logger logger = LoggerFactory.getLogger(TestDriveTests.class);

    @Test
    public void testCalculateDistance() throws Exception
    {
        double startLatitude = 30.25860595703125d;
        double startLongitude = -97.74873352050781d;

        double endLatitude = 30.26068878173828d;
        double endLongitude = -97.7466812133789d;

        //double endLatitude = 44.9441d;
        //double endLongitude = -93.0852d;

        /*Coordinate start = new Coordinate();
        start.setX(startLatitude);
        start.setY(startLongitude);

        Coordinate end = new Coordinate();
        end.setX(endLatitude);
        end.setY(endLongitude);

        CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;

        //CoordinateReferenceSystem crs =  DefaultGeocentricCRS.CARTESIAN;

        //CoordinateReferenceSystem crs = DefaultEngineeringCRS.CARTESIAN_2D;

        //CoordinateReferenceSystem crs = DefaultEngineeringCRS.GENERIC_2D;

        // the following code is based on JTS.orthodromicDistance( start, end, crs )
        GeodeticCalculator gc = new GeodeticCalculator(crs);
        gc.setStartingPosition(JTS.toDirectPosition(start, crs));
        gc.setDestinationPosition(JTS.toDirectPosition(end, crs));

        double distance = gc.getOrthodromicDistance();

        int totalmeters = (int) distance;
        int km = totalmeters / 1000;
        int meters = totalmeters - (km * 1000);
        float remaining_cm = (float) (distance - totalmeters) * 10000;
        remaining_cm = Math.round(remaining_cm);
        float cm = remaining_cm / 100;

        logger.info("Distance = " + km + "km " + meters + "m " + cm + "cm");*/

        double distance = DistanceUtils.distLawOfCosinesRAD(
                DistanceUtils.toRadians(startLatitude),
                DistanceUtils.toRadians(startLongitude),
                DistanceUtils.toRadians(endLatitude),
                DistanceUtils.toRadians(endLongitude));
        distance = DistanceUtils.radians2Dist(distance, DistanceUtils.EARTH_MEAN_RADIUS_KM);
        logger.info("Distance = "+distance);
    }
}
