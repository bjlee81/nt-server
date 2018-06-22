package com.collect.betty;

import com.collect.betty.bootstrap.BettyServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@Slf4j
@SpringBootApplication
@EnableAutoConfiguration
public class WebServerApplication implements CommandLineRunner {
    @Autowired
    private ApplicationContext appContext;

    public static void main(String[] args) {
        SpringApplication.run(WebServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        BettyServer server = appContext.getBean(BettyServer.class);
        server.start();
    }
}
