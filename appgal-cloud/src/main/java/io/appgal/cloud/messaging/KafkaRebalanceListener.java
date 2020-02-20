package io.appgal.cloud.messaging;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class KafkaRebalanceListener implements ConsumerRebalanceListener {
    private static Logger logger = LoggerFactory.getLogger(KafkaRebalanceListener.class);

    private Map<String,List<TopicPartition>> topicPartitions;
    private Boolean active;

    public KafkaRebalanceListener(Map<String, List<TopicPartition>> topicPartitions, Boolean active) {
        this.topicPartitions = topicPartitions;
        this.active = active;
    }

    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> collection) {

        logger.info("********PARTITIONS_REVOKED**********");
        logger.info("************************************");
    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        logger.info("********PARTITIONS_ASSIGNED**********");
        logger.info("************************************");
        this.active = Boolean.TRUE;

        List<TopicPartition> partitionList = Arrays.asList(partitions.toArray(new TopicPartition[0]));
        for (TopicPartition topicPartition : partitionList) {
            String registeredTopic = topicPartition.topic();

            List<TopicPartition> local = topicPartitions.get(registeredTopic);
            if (local != null) {
                local.add(topicPartition);
            } else {
                topicPartitions.put(registeredTopic, Arrays.asList(topicPartition));
            }
            logger.info("******************************************");
            logger.info("NUMBER_OF_PARTITIONS registered for :(" + registeredTopic + ") " + topicPartitions.size());
            logger.info("******************************************");
        }
    }

    @Override
    public void onPartitionsLost(Collection<TopicPartition> partitions) {
        logger.info("********PARTITIONS_LOST**********");
        logger.info("************************************");

    }
}
