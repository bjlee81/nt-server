package com.collect.webapp.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * reactive controller sample
 *
 * Flux, Mono 클래스로 return 을 통해 reactive 를 구현
 *
 */
@RestController
@RequestMapping
class MessageController {
    /**
     * List<Message> 대신에 Flux 로 return한다.
     * @return
     */
    @GetMapping
    Flux<Message> allMessages() {
        return Flux.just(
                Message.builder().body("hello Spring 5").build(),
                Message.builder().body("hello Spring Boot 2").build()
        );
    }
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class Message {
    String body;
}