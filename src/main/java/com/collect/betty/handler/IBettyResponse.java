package com.collect.betty.handler;

import com.collect.betty.type.ApiType;

public interface IBettyResponse {
    AbstractBettyResponse createResponseObject(ApiType apiType);
}