package com.collect.webapp.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * topic을 발행하고 KafkaListener 를 통해 consumer 처리
 * consumer 가 여럿일 경우 offset 은 consumer 별로 따로 관리되지 않는다.
 * topic offset을 따로 관리하려면 consumer group을 지정해야한다.
 *
 */
@SpringBootApplication
@Slf4j
public class ConsumerApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args).close();
    }

    @Value("${kafka.topic.boot}")
    String bootTopic;

    @Autowired
    private KafkaTemplate<String, String> template;

    private final CountDownLatch latch = new CountDownLatch(3);

    @Override
    public void run(String... args) throws Exception {
        this.template.send(bootTopic, "foo1");
        this.template.send(bootTopic, "foo2");
        this.template.send(bootTopic, "foo3");
        latch.await(60, TimeUnit.SECONDS);
        LOGGER.info("All received");
    }

    @KafkaListener(topics = "${kafka.topic.boot}")
    private void listen(ConsumerRecord<?, ?> cr) throws Exception {
        LOGGER.info(cr.toString());
        latch.countDown();
    }
}
