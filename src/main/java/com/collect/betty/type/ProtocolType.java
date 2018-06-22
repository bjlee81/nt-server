package com.collect.betty.type;

/**
 * ProtocolType enumeration
 */
public enum ProtocolType {

    HTTP("HTTP"),
    HTTPS("HTTPS"),
    WEBSOCKET("WEBSOCKET"),
    TCP("TCP");

    private String title;

    ProtocolType(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
