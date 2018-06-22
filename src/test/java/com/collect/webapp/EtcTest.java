package com.collect.webapp;

import org.junit.Test;

import java.util.Arrays;

public class EtcTest {

    @Test
    public void jdk8() {
        Arrays.asList("a1", "a2", "a3").stream()
                .map(s -> s.substring(1))
                .mapToInt(Integer::parseInt)
                .max()
                .ifPresent(System.out::println);
    }
}
