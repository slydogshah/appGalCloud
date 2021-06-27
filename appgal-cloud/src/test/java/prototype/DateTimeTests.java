package prototype;

import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

public class DateTimeTests {
    private static Logger logger = LoggerFactory.getLogger(DateTimeTests.class);

    @Test
    public void testTodayOrTomorrow() throws Exception
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt("8"));
        LocalDateTime left = LocalDateTime.ofInstant(calendar.toInstant(),ZoneOffset.UTC);
        LocalDateTime leftUtc = left.toLocalDate().atStartOfDay();

        LocalDateTime right = LocalDateTime.ofInstant(calendar.toInstant(),ZoneOffset.UTC);
        OffsetDateTime tom = OffsetDateTime.ofInstant(right.plus(0, ChronoUnit.DAYS).toInstant(ZoneOffset.UTC),ZoneOffset.UTC);
        LocalDateTime rightUtc = tom.toLocalDate().atStartOfDay();
        boolean today = false;

        logger.info(leftUtc.toEpochSecond(ZoneOffset.UTC)+"");
        logger.info(rightUtc.toEpochSecond(ZoneOffset.UTC)+"");

        if(leftUtc.toEpochSecond(ZoneOffset.UTC) == rightUtc.toEpochSecond(ZoneOffset.UTC))
        {
            logger.info("TODAY");
        }
        else
        {
            logger.info("TOMORROW");
        }
    }
}
