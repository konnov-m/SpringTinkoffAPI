package org.example.kafka.consumer;

public class ConsumerException extends RuntimeException {
    public ConsumerException(String message, Throwable cause) {
        super(message, cause);
    }
}
