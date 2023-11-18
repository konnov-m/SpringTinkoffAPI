package org.example;

import org.example.kafka.consumer.MyConsumer;
import org.example.kafka.consumer.StringValueConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        var consumer = new MyConsumer("kafka:29092");

        var dataConsumer = new StringValueConsumer(consumer, value -> log.info("value:{}", value));
        dataConsumer.startSending();
    }
}