package com.adacorp.task_managementV2.kafka.kafkaService;

public interface PokemonKafkaConsumerService {
    void consume();
    void consume(String message);
}
