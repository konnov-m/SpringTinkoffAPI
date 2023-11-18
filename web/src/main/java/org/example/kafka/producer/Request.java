package org.example.kafka.producer;

import java.util.List;

public record Request(String func, List<String> params) {}
