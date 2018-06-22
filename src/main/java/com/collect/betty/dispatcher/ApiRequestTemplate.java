package com.collect.betty.dispatcher;

import java.util.Map;

import com.collect.betty.service.RequestParamException;
import com.collect.betty.service.ServiceException;
import lombok.extern.slf4j.Slf4j;

import com.google.gson.JsonObject;

/**
 * ApiRequest 를 구현한 추상클래스
 */
@Slf4j
public abstract class ApiRequestTemplate implements ApiRequest {

    /**
     * API 요청 data
     */
    protected Map<String, Object> reqData;

    /**
     * API 처리결과
     */
    protected JsonObject apiResult;

    /**
     * logger 생성<br/>
     * apiResult 객체 생성
     */
    public ApiRequestTemplate(Map<String, Object> reqData) {
        this.apiResult = new JsonObject();
        this.reqData = reqData;

        LOGGER.info("request data : " + this.reqData);
    }

    /**
     * 서비스 요청 결과 처리를 위한 step
     */
    public void executeService() {
        try {
            this.requestParamValidation();
            this.service();
        } catch (RequestParamException e) {
            LOGGER.error(e.getMessage());
            this.apiResult.addProperty("resultCode", "405");
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage());
            this.apiResult.addProperty("resultCode", "501");
        }
    }

    public JsonObject getApiResult() {
        return this.apiResult;
    }


    // 요청맵의 정합성을 검증한다.
    @Override
    public void requestParamValidation() throws RequestParamException {
        if (getClass().getClasses().length == 0) {
            return;
        }

        // // TODO 이건 꼼수 바꿔야 하는데..
        // for (Object item :
        // this.getClass().getClasses()[0].getEnumConstants()) {
        // RequestParam param = (RequestParam) item;
        // if (param.isMandatory() && this.reqData.get(param.toString()) ==
        // null) {
        // throw new RequestParamException(item.toString() +
        // " is not present in request param.");
        // }
        // }
    }

    public final <T extends Enum<T>> T fromValue(Class<T> paramClass, String paramValue) {
        if (paramValue == null || paramClass == null) {
            throw new IllegalArgumentException("There is no value with name '" + paramValue + " in Enum "
                    + paramClass.getClass().getName());
        }

        for (T param : paramClass.getEnumConstants()) {
            if (paramValue.equals(param.toString())) {
                return param;
            }
        }

        throw new IllegalArgumentException("There is no value with name '" + paramValue + " in Enum "
                + paramClass.getClass().getName());
    }
}