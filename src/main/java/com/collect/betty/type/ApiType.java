package com.collect.betty.type;

/**
 * ApiType enumeration
 */
public enum ApiType {

    JSON("json"),
    XML("spear"),
    PLAINT_TEXT("axe"),
    UNDEFINED("");

    private String title;

    ApiType(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
