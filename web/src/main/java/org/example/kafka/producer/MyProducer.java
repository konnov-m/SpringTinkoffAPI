package org.example.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.CLIENT_ID_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.RETRIES_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.*;
import static org.example.kafka.producer.JsonSerializer.OBJECT_MAPPER;

public class MyProducer {
    private static final Logger log = LoggerFactory.getLogger(MyProducer.class);
    private final KafkaProducer<Long, Request> kafkaProducer;

    public static final String TOPIC_NAME = "topicAppToInvest";
    public static final String SERVERS_CONFIG = "kafka:29092";

    public MyProducer() {
        Properties props = new Properties();
        props.put(CLIENT_ID_CONFIG, "myKafkaProducer");
        props.put(BOOTSTRAP_SERVERS_CONFIG, SERVERS_CONFIG);
        props.put(ACKS_CONFIG, "1");
        props.put(RETRIES_CONFIG, 1);
        props.put(BATCH_SIZE_CONFIG, 16384);
        props.put(LINGER_MS_CONFIG, 10);
        props.put(BUFFER_MEMORY_CONFIG, 33_554_432); // bytes
        props.put(MAX_BLOCK_MS_CONFIG, 1_000); // ms
        props.put(KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(OBJECT_MAPPER, new ObjectMapper());

        kafkaProducer = new KafkaProducer<>(props);

        var shutdownHook = new Thread(() -> {
            log.info("closing kafka producer");
            kafkaProducer.close();
        });
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    public KafkaProducer<Long, Request> getMyProducer() {
        return kafkaProducer;
    }
}
