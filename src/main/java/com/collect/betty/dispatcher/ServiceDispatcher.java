package com.collect.betty.dispatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Service class dispatcher by uri
 *
 * @author kris
 */
@Component
public class ServiceDispatcher {
    private static ApplicationContext springContext;

    /*
     *  springcontext 는 정적 변수에 직접 할당할 수 없음으로 간접 할당
     */
    @Autowired
    public void init(ApplicationContext springContext) {
        ServiceDispatcher.springContext = springContext;
    }

    /**
     * 요청에서 처리한 map을 통해 doDispatch 처리 한다.
     * start uri 로 판단하기에 별도의 Service 단에서 sub context를 구현해야한다.
     *
     * @param requestMap handler 에서 처리된 requestMap
     * @return
     */
    public static ApiRequest doDispatch(Map<String, Object> requestMap) {
        String serviceUri = (String) requestMap.get("REQUEST_URI");
        String beanName = null;

        // uri 가 없으면 요청 not found
        if (serviceUri == null) {
            beanName = "notFound";
        }

        // request uri 별 분기 처리
        // restful 디자인에 맞게 각 요청별 method로 처리하도록 한다.
        if (serviceUri.startsWith("/tokens")) {
            String httpMethod = (String) requestMap.get("REQUEST_METHOD");
            switch (httpMethod) {
                case "POST":
                    beanName = "tokenIssue";
                    break;
                case "DELETE":
                    beanName = "tokenExpier";
                    break;
                case "GET":
                    beanName = "tokenVerify";
                    break;

                default:
                    beanName = "notFound";
                    break;
            }
        } else if (serviceUri.startsWith("/users")) {
            beanName = "users";
        } else {
            beanName = "notFound";
        }

        ApiRequest service = null;
        try {
            // ApiRequest 를 구현한 Service 들의 Bean을 얻어와 처리한다.
            service = (ApiRequest) springContext.getBean(beanName, requestMap);
        } catch (Exception e) {
            e.printStackTrace();
            service = (ApiRequest) springContext.getBean("notFound", requestMap);
        }

        return service;
    }
}