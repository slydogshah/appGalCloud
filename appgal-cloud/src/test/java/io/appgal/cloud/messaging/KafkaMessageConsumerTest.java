package io.appgal.cloud.messaging;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

//TODO:Look at removing dependency on KafkaServer (@bugs.bunny.shah@gmail.com)

@QuarkusTest
public class KafkaMessageConsumerTest {
    private static Logger logger = LoggerFactory.getLogger(KafkaMessageConsumerTest.class);

    @Inject
    private KafkaMessageConsumer kafkaMessageConsumer;

    @Test
    public void test()
    {
        this.kafkaMessageConsumer.consumeData();
    }
}
