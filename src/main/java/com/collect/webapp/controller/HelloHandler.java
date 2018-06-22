package com.collect.webapp.controller;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * @RestController 가 아닌 router functional 방식의 Component 선언
 * <p>
 * Mono Class 는 한건의 데이터(“at most one”)를 처리하는데 사용함
 * Flux Class 는 N개의 element 를 가질 수 있고 여러 건을 처리함
 */
@Component
public class HelloHandler {

    private final HelloWorldRepository hellos;

    public HelloHandler(HelloWorldRepository hellos) {
        this.hellos = hellos;
    }

    public Mono<ServerResponse> hello(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN)
                .body(BodyInserters.fromObject("Hello, Spring reactive Mono!"));
    }

    public Mono<ServerResponse> create(ServerRequest req) {
        return req.bodyToMono(HelloWorld.class)
                .flatMap(helloWorld -> this.hellos.save(helloWorld))
                // ServerResponse.created(location) 은 201 http status 와 함께 responsebody 를 생성
                // location 값에 대한 헤더값을 생성
                .flatMap(helloWorld -> ServerResponse.created(URI.create("/hello/" + helloWorld.getId())).build());
    }

    public Mono<ServerResponse> get(ServerRequest req) {
        // get을 통해 들어온 id 값이 있는데...
        Mono<HelloWorld> helloworldMono = Mono.just(new HelloWorld("test", "hello", "hi"));
        return ServerResponse.ok().body(helloworldMono, HelloWorld.class);
    }

    public Mono<ServerResponse> update(ServerRequest req) {
        return Mono
                .zip(
                        (data) -> {
                            HelloWorld p = (HelloWorld) data[0];
                            HelloWorld p2 = (HelloWorld) data[1];
                            p.setTitle(p2.getTitle());
                            p.setMessage(p2.getMessage());
                            return p;
                        },
                        this.hellos.findById(req.pathVariable("id")),
                        req.bodyToMono(HelloWorld.class)
                )
                .cast(HelloWorld.class)
                .flatMap(helloWorld -> this.hellos.save(helloWorld))
                .flatMap(helloWorld -> ServerResponse.noContent().build());

    }

    public Mono<ServerResponse> delete(ServerRequest req) {
        return ServerResponse.noContent().build(this.hellos.deleteById(req.pathVariable("id")));
    }

    public Mono<ServerResponse> all(ServerRequest req) {
        return ServerResponse.ok().body(this.hellos.findAll(), HelloWorld.class);
    }

}

interface HelloWorldRepository extends ReactiveMongoRepository {
}