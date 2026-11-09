package com.adacorp.task_managementV2.kafka.kafkaService.kafkaServiceImpl;

import com.adacorp.task_managementV2.kafka.kafkaService.PokemonKafkaConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PokemonKafkaConsumerServiceImpl implements PokemonKafkaConsumerService {
    @Override
    public void consume() {

    }

    /**
     * La méthode : est automatiquement appelée.
     **/
    @Override
    @KafkaListener( topics = "test-json-format4", groupId = "pokemon-consumer-group" )
    public void consume(String message) {

        log.info("Message reçu depuis Kafka : {}", message);
        log.info("Message reçu : {}", message);
    }
}

/*
 * Dans une autre console :
 * kafka-console-producer.bat --bootstrap-server localhost:9092 --topic test-json-format4
 **/
