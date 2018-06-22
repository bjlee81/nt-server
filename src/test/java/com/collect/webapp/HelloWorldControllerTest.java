package com.collect.webapp;

import com.collect.webapp.controller.HelloWorld;
import com.collect.webapp.controller.HelloHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@WebFluxTest
@Import(value = HelloHandler.class)
public class HelloWorldControllerTest {
    @Autowired
    private WebTestClient webClient;

    @Test
    public void test_HelloWorldRouter() {
        // WebClient로 "hello"라는 URL을 호출
        HelloWorld responseBody = webClient.get().uri("/hello").exchange()
                .expectStatus().isOk() // 응답이 200인지 확인
                .expectBody(HelloWorld.class) // 리턴하는 객체가 HelloWorld 클래스인지 확인
                .returnResult().getResponseBody();

        // reponse된 객체에 원하는 결과값이 들어있는지 assert함.
        assertThat(responseBody.getId()).isEqualTo(1);
        assertThat(responseBody.getTitle()).isEqualTo("hello");
        assertThat(responseBody.getMessage()).isEqualTo("hi");
    }     
    
    @Test
    public void test_FailExecutePostMethod() {
        // POST는 지원하지 않는 method
        webClient.post().uri("/hello").exchange()
                .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }
}