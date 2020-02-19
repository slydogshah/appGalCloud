package io.appgal.cloud.messaging;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class KafkaRebalanceListener implements ConsumerRebalanceListener {
    private static Logger logger = LoggerFactory.getLogger(KafkaRebalanceListener.class);

    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> collection) {

        logger.info("********PARTITIONS_REVOKED**********");
        logger.info("************************************");
    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> collection) {
        logger.info("********PARTITIONS_ASSIGNED**********");
        logger.info("************************************");

    }

    @Override
    public void onPartitionsLost(Collection<TopicPartition> partitions) {
        logger.info("********PARTITIONS_LOST**********");
        logger.info("************************************");

    }
}
