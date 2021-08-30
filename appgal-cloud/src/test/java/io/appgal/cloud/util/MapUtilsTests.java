package io.appgal.cloud.util;

import io.appgal.cloud.model.Address;
import io.appgal.cloud.model.Location;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.time.*;

import static java.time.temporal.ChronoUnit.*;

import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@QuarkusTest
public class MapUtilsTests {
    private static Logger logger = LoggerFactory.getLogger(MapUtilsTests.class);

    @Inject
    private MapUtils mapUtils;

    @Test
    public void calculateCoordinates() throws Exception
    {
        Address address = new Address();
        address.setStreet("801 West Fifth Street");
        address.setZip("78703");
        Location location = this.mapUtils.calculateCoordinates(address);
        JsonUtil.print(this.getClass(),location.toJson());
    }

    @Test
    public void determineTimeZone() throws Exception
    {
        double latitude = 30.2698104d;
        double longitude = -97.75115579999999d;
        ZoneId timezone = this.mapUtils.determineTimeZone(latitude,longitude);
        logger.info("TIME_ZONE: "+timezone);
    }

    @Test
    public void dateFormatting() throws Exception
    {
        /*Calendar calendar = Calendar.getInstance();
        for(int i=0; i<24; i++)
        {
            calendar.set(Calendar.HOUR_OF_DAY, i);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:00 a");
            //System.out.println(simpleDateFormat.format(new Date(calendar.getTimeInMillis())));
            //System.out.println(simpleDateFormat.format(new Date(dateTime.toInstant().toEpochMilli())));
        }*/

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, 18);
        LocalDateTime localDate = LocalDateTime.ofInstant(calendar1.toInstant(),ZoneOffset.UTC);
        OffsetDateTime dateTime = OffsetDateTime.of(localDate,ZoneOffset.UTC);
        System.out.println(dateTime.toEpochSecond());

        OffsetDateTime tomorrow = OffsetDateTime.ofInstant(dateTime.toInstant().plus(1, DAYS),ZoneOffset.UTC);
        System.out.println(tomorrow.toEpochSecond());

        assertNotEquals(dateTime.toEpochSecond(),tomorrow.toEpochSecond());
    }
}
