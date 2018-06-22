package com.collect.webapp.controller;

import com.collect.webapp.KafkaSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/mobile/")
public class MoblieController {

    @Autowired
    KafkaSender kafkaSender;
//
//    @GetMapping(value = "/cs-alloc/")
//    public String csAlloc(HttpServletRequest request, @RequestParam("payload") String payload) {
//        Map headerMap = getRequestHeader(request);
//
//        /**
//         * ○ 분산서버 의 HTTP 응답메시지(분산서버 ➞ 단말기)
//         * HTTP/1.1 200 OK
//         * Date: Wed Jul 4 10:20:14 KST 2012
//         * Server: VSLB/1.0
//         * Content-Type: application/sdp
//         * Connection: close
//         * Content-Length: 178
//         * v=0
//         * o=vdis 1234567890 1234567890 IN IP4 192.168.0.1
//         * s=vehicle driving information gathering session
//         * c=IN IP4 192.168.10.1
//         * m=application 30000 tcp
//         * a=vdr-blk:180
//         * a=row-vdr:1
//         *
//         * VSLB/1.0 분산서버 이름과 버전
//         * 178 응답메시지의 SDP의 길이
//         * 192.168.0.1 수집서버의 IP
//         * 30000 수집서버의 port
//         * 180 데이터 전송주기
//         * (수집데이터가 전송주기만큼 모여야 전송할 수 있다.)
//         * 1 데이터 수집주기(초단위)
//         */
//
//
//        // json 의 형태로 topic을 쏴주면 될거같은데
//        // partition 별로 순서를 보장하기에 consumer 에서 partition 별로 들어가 있는 topic들을 어떻게 잘 조합할건지 고민필요.
//        kafkaSender.send(payload);
//        return "Message sent to the Kafka Topic TIMS Successfully";
//    }
//
//    private Map getRequestHeader(HttpServletRequest request) {
//        String userAgent = request.getHeader("User-Agent");
//        return null;
//    }

}