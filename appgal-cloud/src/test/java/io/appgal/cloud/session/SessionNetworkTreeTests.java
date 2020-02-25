package io.appgal.cloud.session;

import com.google.gson.JsonObject;
import io.appgal.cloud.messaging.MessageWindow;
import io.appgal.cloud.model.SourceNotification;
import org.geotools.data.DataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.memory.MemoryDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.collection.BaseSimpleFeatureCollection;
import org.geotools.feature.collection.SimpleFeatureIteratorImpl;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.action.SafeAction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.spatial4j.distance.DistanceUtils;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.*;
import java.util.*;
import java.util.List;

public class SessionNetworkTreeTests {
    private static Logger logger = LoggerFactory.getLogger(SessionNetworkTreeTests.class);

    SessionNetwork sessionNetwork = new SessionNetwork();

    @BeforeEach
    public void setUp() throws Exception
    {
        TreeMap<String, FoodRunnerSession> foodRunnerSessions = sessionNetwork.getFoodRunnerSessions();

        for(int x=0; x<5; x++) {
            FoodRunnerSession foodRunnerSession = new FoodRunnerSession();
            String foodRunnerId = UUID.randomUUID().toString() + "/" + "foodRunnerId";
            String foodRunnerSessionId = UUID.randomUUID().toString() + "/" + "foodRunnerSessionId";
            foodRunnerSession.setFoodRunnerId(foodRunnerId);
            foodRunnerSession.setFoodRunnerSessionId(foodRunnerSessionId);

            for (int i = 0; i < 10; i++) {
                /*LocalDateTime startOfLocalDateInUtc = LocalDate.now(ZoneOffset.UTC).atStartOfDay();
                OffsetDateTime startTime = OffsetDateTime.of(startOfLocalDateInUtc, ZoneOffset.UTC);
                OffsetDateTime endTime = OffsetDateTime.now(ZoneOffset.UTC);*/
                OffsetDateTime startTime = OffsetDateTime.now(ZoneOffset.UTC);
                OffsetDateTime endTime = startTime.plusMinutes(Duration.ofMinutes(10).toMinutes());
                MessageWindow messageWindow = new MessageWindow(startTime, endTime);

                String sourceNotificationId = UUID.randomUUID().toString();
                SourceNotification sourceNotification = new SourceNotification();
                sourceNotification.setSourceNotificationId(sourceNotificationId);
                sourceNotification.setMessageWindow(messageWindow);
                if(i % 2 == 0) {
                    sourceNotification.setLatitude("46.066667");
                    sourceNotification.setLongitude("11.116667");
                }
                else
                {
                    sourceNotification.setLatitude("44.9441");
                    sourceNotification.setLongitude("-93.0852");
                }

                JsonObject notification = new JsonObject();
                notification.addProperty("sourceNotificationId", sourceNotificationId);
                notification.addProperty("latitude", sourceNotification.getLatitude());
                notification.addProperty("longitude", sourceNotification.getLongitude());
                messageWindow.addMessage(notification);

                foodRunnerSession.receiveNotifications(messageWindow);

                foodRunnerSessions.put(foodRunnerSession.getFoodRunnerSessionId(), foodRunnerSession);
            }

            Thread.sleep(1000);
        }

        //Time to assert
        Iterator<FoodRunnerSession> iterator = foodRunnerSessions.values().iterator();
        while(iterator.hasNext()) {
            FoodRunnerSession foodRunnerSession = iterator.next();
            Map<String, List<SourceNotification>> sourceNotifications = foodRunnerSession.getSourceNotifications();
            Set<Map.Entry<String, List<SourceNotification>>> entrySet = sourceNotifications.entrySet();
            for (Map.Entry<String, List<SourceNotification>> entry : entrySet) {
                List<SourceNotification> notifications = entry.getValue();
                logger.info(notifications.toString());
                logger.info(notifications.iterator().next().getMessageWindow().toString());
            }
        }
    }

    @AfterEach
    public void tearDown()
    {
        this.sessionNetwork = null;
    }

    @Test
    public void testFindSessionsAroundSource() throws Exception
    {
        String latitude = "46.066667";
        String longitude = "11.116667";

        TreeMap<String, FoodRunnerSession> foodRunnerSessions = this.sessionNetwork.getFoodRunnerSessions();

        List<FoodRunnerSession> sessionsThatMeetCriteria = new ArrayList<>();

        Set<Map.Entry<String, FoodRunnerSession>> entrySet = foodRunnerSessions.entrySet();
        for(Map.Entry<String, FoodRunnerSession> entry: entrySet)
        {
            FoodRunnerSession foodRunnerSession = entry.getValue();

            //Check the criteria and decide if this session qualifies
            Collection<List<SourceNotification>> sourceNotifications = foodRunnerSession.getSourceNotifications().values();
            for(List<SourceNotification> list:sourceNotifications)
            {
                for(SourceNotification sourceNotification:list)
                {
                    String sourceNotificationLat = sourceNotification.getLatitude();
                    String sourceNotificationLon = sourceNotification.getLongitude();
                    if(sourceNotificationLat.equals(latitude) && sourceNotificationLon.equals(longitude))
                    {
                        sessionsThatMeetCriteria.add(foodRunnerSession);
                    }
                }
            }
        }

        logger.info("Result Size: "+sessionsThatMeetCriteria.size());
        this.plotSessions(sessionsThatMeetCriteria);
    }

    private void plotSessions(List<FoodRunnerSession> sessionsThatMeetCriteria) throws Exception
    {
        double sourceLatitude = 46.066667d;
        double sourceLongitude = 11.116667d;

        // Set cross-platform look & feel for compatability
        //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

        final SimpleFeatureType TYPE =
                DataUtilities.createType(
                        "Location",
                        "the_geom:Point:"
                                + // <- the geometry attribute: Point type
                                "name:String,"
                                + // <- a String attribute
                                "number:Integer" // a number attribute
                );

        /*
         * A list to collect features as we create them.
         */
        List<SimpleFeature> features = new ArrayList<>();
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
        for(FoodRunnerSession foodRunnerSession:sessionsThatMeetCriteria)
        {
            Map<String, List<SourceNotification>> sourceNotifications = foodRunnerSession.getSourceNotifications();
            Set<Map.Entry<String, List<SourceNotification>>> entrySet = sourceNotifications.entrySet();
            for(Map.Entry<String, List<SourceNotification>> entry:entrySet)
            {
                List<SourceNotification> notifications = entry.getValue();
                for(SourceNotification notification:notifications) {
                    double latitude = Double.parseDouble(notification.getLatitude());
                    double longitude = Double.parseDouble(notification.getLongitude());
                    String name = notification.getSourceNotificationId();

                    /* Longitude (= x coord) first ! */
                    Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));

                    featureBuilder.add(point);
                    featureBuilder.add(name);
                    SimpleFeature feature = featureBuilder.buildFeature(null);
                    features.add(feature);

                    double distanceToSource = this.calculateDistance(sourceLatitude,sourceLongitude,latitude,longitude);
                    logger.info("****");
                    logger.info("DistanceToSource: "+distanceToSource);
                    logger.info("****");
                }
            }
        }

        //Plot the FoodRunnerSessions
        //this.renderFeatures(TYPE, features);
    }

    private void renderFeatures(SimpleFeatureType type, List<SimpleFeature> features) throws Exception
    {
        //Get the Feature Collection
        SimpleFeatureCollection featureCollection = new BaseSimpleFeatureCollection(type) {
            @Override
            public SimpleFeatureIterator features() {
                return new SimpleFeatureIteratorImpl(features);
            }
        };

        // Create a map context and add our shapefile to it
        DataStore memoryDataStore = new MemoryDataStore(featureCollection);
        String typeName = memoryDataStore.getTypeNames()[0];
        MapContent map = new MapContent();
        final Style style = SLD.createSimpleStyle(memoryDataStore, typeName, Color.BLACK);
        Layer layer = new FeatureLayer(featureCollection, style);
        map.layers().add(layer);

        // Create a JMapFrame with custom toolbar buttons
        JMapFrame mapFrame = new JMapFrame(map);
        mapFrame.enableToolBar(true);
        mapFrame.enableStatusBar(true);

        JToolBar toolbar = mapFrame.getToolBar();
        toolbar.addSeparator();
        toolbar.add(new JButton(new SessionNetworkTreeTests.ValidateGeometryAction(featureCollection)));
        //toolbar.add(new JButton(new ExportShapefileAction()));

        // Display the map frame. When it is closed the application will exit
        mapFrame.setSize(800, 600);
        mapFrame.setVisible(true);

        while(true);
    }

    class ValidateGeometryAction extends SafeAction {
        private SimpleFeatureCollection simpleFeatureCollection;

        ValidateGeometryAction(SimpleFeatureCollection simpleFeatureCollection) {
            super("Validate geometry");
            this.simpleFeatureCollection = simpleFeatureCollection;
            putValue(Action.SHORT_DESCRIPTION, "Check each geometry");
        }

        public void action(ActionEvent e) throws Throwable {
            int numInvalid = validateFeatureGeometry(simpleFeatureCollection);
            String msg;
            if (numInvalid == 0) {
                msg = "All feature geometries are valid";
            } else {
                msg = "Invalid geometries: " + numInvalid;
            }
            JOptionPane.showMessageDialog(
                    null, msg, "Geometry results", JOptionPane.INFORMATION_MESSAGE);
        }

        private int validateFeatureGeometry(SimpleFeatureCollection simpleFeatureCollection) {

            int numberValid = 0;

            SimpleFeatureIterator iterator = simpleFeatureCollection.features();
            while(iterator.hasNext())
            {
                SimpleFeature feature = iterator.next();

                logger.info(feature.toString());

                numberValid ++;
            }

            return numberValid;
        }
    }

    private double calculateDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude)
    {
        double distance = DistanceUtils.distLawOfCosinesRAD(
                DistanceUtils.toRadians(startLatitude),
                DistanceUtils.toRadians(startLongitude),
                DistanceUtils.toRadians(endLatitude),
                DistanceUtils.toRadians(endLongitude));
        distance = DistanceUtils.radians2Dist(distance, DistanceUtils.EARTH_MEAN_RADIUS_MI);
        return distance;
    }
}
