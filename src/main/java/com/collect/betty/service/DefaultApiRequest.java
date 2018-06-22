package com.collect.betty.service;

import java.util.Map;

import com.collect.betty.dispatcher.ApiRequestTemplate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("notFound")
@Scope("prototype")
public class DefaultApiRequest extends ApiRequestTemplate {

    public DefaultApiRequest(Map<String, Object> reqData) {
        super(reqData);
    }

    @Override
    public void service() {
        this.apiResult.addProperty("resultCode", "404");
    }
}