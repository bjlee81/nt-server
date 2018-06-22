package com.collect.webapp.router;

import com.collect.webapp.controller.HelloHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * web flux 사용을 위한 config 설정
 */
@Configuration
// spring 5 reactive web flux
public class HelloRouter {

    // spring boot 2 Request Route Function
    // routerfunction이 handler component 와 연결
    @Bean
    public RouterFunction<ServerResponse> route(HelloHandler handler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/hello")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), handler::hello)
                .andRoute(RequestPredicates.POST("/hello"), handler::create)
                .andRoute(RequestPredicates.GET("/hello/{id}"), handler::get)
                .andRoute(RequestPredicates.PUT("/hello/{id}"), handler::update)
                .andRoute(RequestPredicates.DELETE("/hello/{id}"), handler::delete);
    }

    @Bean
    public RouterFunction<ServerResponse> route() {
        return RouterFunctions.route(RequestPredicates.GET("/"),
                (req) -> ok().body(BodyInserters.fromObject(new String("Welcome!!")))
        );
    }
}