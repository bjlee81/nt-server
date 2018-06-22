package com.collect.webapp.controller;

import com.collect.webapp.KafkaSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/tims-kafka/")
public class Boot2WebController {

    @Autowired
    KafkaSender kafkaSender;

    @GetMapping(value = "/producer")
    public String producer(@RequestParam("payload") String payload) {

        kafkaSender.send(payload);
        return "Message sent to the Kafka Topic TIMS Successfully";
    }

}