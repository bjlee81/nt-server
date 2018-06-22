package com.collect.webapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CSAllocService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.topic.tims}")
    private String topic;

    public void send(String msg) {
        LOGGER.info("sending payload='{}' to topic='{}'", msg, topic);
        kafkaTemplate.send(topic, msg);

        // kafka consumer 를 통해 해당 topic을 얻는다.
        // kafka-console-consumer.bat localhost:9092 --bootstrap-server --topic tims_topic --from-beginning
        // storm spout에서 가져가도 될듯

    }
}