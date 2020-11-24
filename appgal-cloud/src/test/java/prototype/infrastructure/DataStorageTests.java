package prototype.infrastructure;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusTest
public class DataStorageTests {
    private static Logger logger = LoggerFactory.getLogger(DataStorageTests.class);

    @Test
    public void testProduceData() throws Exception
    {
        logger.info("***************");
    }
}
