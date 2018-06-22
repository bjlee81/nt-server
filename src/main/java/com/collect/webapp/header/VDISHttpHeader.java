package com.collect.webapp.header;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public final class VDISHttpHeader implements Serializable {
    /**
     * 단말기 --> 분산서버
     */
    private String host;
    // 단말기의 정보
    private String userAgent = "R1/v123";
    // 개통한 모뎀번호
    private String xOid;
    private String sdp_contentType = "application/sdp";
    /**
     * 단말기 --> 수집서버
     */
    private String vdrb_contentType = "application/x-vdrb";
    private static final String[] bodyType = {"taxi/dtg", "taxi/event"};

    /**
     * 분산서버 --> 단말기
     */
    // 분산서버의 이름과 버전. VSLB/1.0
    private String server;
    // 응답메시지의 SDP의 길이
    private String contentLength;
    private String v = "v=0";
    // 수집서버의 IP
    @NotNull
    private String server_ip;
    // 수집서버의 port
    @NotNull
    private String server_port;
    // o 필드에 추가로 수집서버의 IP 가 추가됨
    private String o;
    private String s = "s=vehicle driving information gathering session";
    // m 필드에 추가로 수집서버의 port 가 추가됨
    private String m;
    // 전송 주기(개). 180
    @NotNull
    private String send_cycle;
    // 수집 주기(초). 1
    @NotNull
    private String collect_cycle;
    // 데이터 전송 주기
    private String a1;
    private String a2;
    /**
     * 수집서버 --> 단말기
     */
    private String responseCode = "200 ok";

    public VDISHttpHeader(@NotNull String server_ip, @NotNull String server_port, @NotNull String send_cycle, @NotNull String collect_cycle) {
        this.server_ip = server_ip;
        this.server_port = server_port;
        this.send_cycle = send_cycle;
        this.collect_cycle = collect_cycle;
        this.o = "o=vdis 1234567890 1234567890 IN IP4 " + server_ip;
        this.m = "m=application " + server_port + " tcp";
        this.a1 = "a=vdr-blk:" + send_cycle;
        this.a2 = "a=row-vdr:" + collect_cycle;
    }

    /**
     400 Bad Request HTTP Header에 ABNF에 맞지 않는 문자열이 포함되었거나, 필수 Header가 누락되었다.
     403 Forbidden 허용되지 않는 단말기로 부터의 요청일 때
     404 Not Found HTTP 요청에서 명시한 Resource가 HTTP server에 존재하지 않을 때.
     415 Unsupported media type Content-Type이 지원하지 않는 타입일 때.
     493 Undecipherable Content-type 에 따라 데이터를 Decode 하려고 시도했으나, 이를 실패한 경우
     500 Server Internal Error Server의 점검 혹은 예기치 않은 사유로 요청을 처리하지못할 때.
     503 Server Overloaded Server가 정상적이나, 과도한 메시지를 수신하여 일시적으로 지연될 때.
     513 Message Too Large 수신한 HTTP 메시지의 크기가 서버에서 처리할 수 있는 길이를 초과하거나
     , 하나의 Header가 서버에서 처리 할 수 있는 길이를 초과하는 경우
     414 Request-URI Too Long HTTP 메시지의 Requesr URI 길이가 1024 바이트가 넘을 때
     406 Not Acceptable Content-Type이 HTTP Head에 없을 때
     493 Undecipherable DTG Header 정보가 규격과 맞지 않을 때. 자동차등록번호가 euc-kr 인코딩이 아닐 때.
     HTTP Content-Length의 길이와 HTTP Body의 길이가 다를 때.
     488 Not Acceptable Here DTG초단위 데이터가 시간 순으로 정렬이 되어있지 않을 때.
     */
}
