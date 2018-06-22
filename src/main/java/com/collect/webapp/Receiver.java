package com.collect.webapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * topic에 대한 cunsumer 서비스를 등록
 *
 */
@Service
public class Receiver {

    private static final Logger LOG = LoggerFactory.getLogger(Receiver.class);

    /**
     * 해당 topic에 대한 consumer listener 를 등록하여 처리된다
     *
     * @param message
     * @param headers
     */
    @KafkaListener(topics = "${app.topic.tims}")
    public void receive(@Payload String message, @Headers MessageHeaders headers) {
        LOG.info("received message='{}'", message);
        headers.keySet().forEach(key -> LOG.info("{}: {}", key, headers.get(key)));
    }

}