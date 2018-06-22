package com.collect.betty.dispatcher;

import com.collect.betty.service.RequestParamException;
import com.collect.betty.service.ServiceException;
import com.google.gson.JsonObject;

/**
 * API Service request worker interface
 *
 * @author kris
 */
public interface ApiRequest {
    /**
     * Request param null check method.
     *
     * 파라미터 값이 입력되었는지 검증
     *
     * @throws RequestParamException
     */
    void requestParamValidation() throws RequestParamException;

    /**
     * 서비스 구현
     *
     * @throws Exception
     */
    void service() throws ServiceException;

    /**
     * API 서비스 호출시 실행.
     *
     * @throws Exception
     */
    void executeService();

    /**
     * API 서비스 수행 결과 조회.
     *
     * @return
     */
    JsonObject getApiResult();
}