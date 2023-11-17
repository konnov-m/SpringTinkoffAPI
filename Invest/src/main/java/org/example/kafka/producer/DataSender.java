package org.example.kafka.producer;

import com.google.protobuf.GeneratedMessageV3;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import static org.example.kafka.producer.MyProducer.TOPIC_NAME;


public class DataSender {
    private static final Logger log = LoggerFactory.getLogger(DataSender.class);

    private final MyProducer myProducer;
    private final Consumer<GeneratedMessageV3> sendAsk;

    private final AtomicLong ID = new AtomicLong(1);

    public DataSender(MyProducer myProducer, Consumer<GeneratedMessageV3> sendAsk) {
        this.sendAsk = sendAsk;
        this.myProducer = myProducer;
    }

    public void dataHandler(GeneratedMessageV3 value) {
        log.info("value:{}", value);
        try {
            myProducer
                    .getMyProducer()
                    .send(new ProducerRecord<>(TOPIC_NAME, ID.getAndIncrement(), value), (metadata, exception) -> {
                        if (exception != null) {
                            log.error("message wasn't sent", exception);
                        } else {
                            log.info("message id:{} was sent, offset:{}", ID.getAndIncrement(), metadata.offset());
                            sendAsk.accept(value);
                        }
                    });
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
