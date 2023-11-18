package org.example.kafka.consumer;

import java.util.List;

public record Request(String func, List<String> params) {}
