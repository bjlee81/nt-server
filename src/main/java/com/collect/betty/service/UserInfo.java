package com.collect.betty.service;

import com.collect.betty.dispatcher.ApiRequestTemplate;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

// users 라는 bean 이름으로 UserInfo 서비스 객체 등록
@Service("users")
// 객체를 요청할 떄 마다 생성하도록 prototype 으로 선언
@Scope("prototype")
public class UserInfo extends ApiRequestTemplate {

    @Autowired
    private SqlSession sqlSession;

    public UserInfo(Map<String, Object> reqData) {
        super(reqData);
    }

    /**
     * header 에 선언되어 있어야 한다.
     *
     * @throws RequestParamException
     */
    @Override
    public void requestParamValidation() throws RequestParamException {
        if (StringUtils.isEmpty(this.reqData.get("email"))) {
            throw new RequestParamException("email이 없습니다.");
        }
    }

    @Override
    public void service() throws ServiceException {
        // 입력 email 사용자의 이메일을 HTTP header 에 입력한다.
        // 출력 resultCode API 처리 결과코드를 돌려준다. API 처리결과가 정상이면 결과코드는 200 이다.
        // 출력 message API 처리 결과 메시지를 돌려준다. API 처리결과가 정상일 때는 Success 메시지를 돌려주며
        // 나머지 정상이 아닐 때는 오류 메시지를 돌려준다.
        // 출력 userNo 입력된 이메일에 해당하는 사용자의 사용자 번호를 돌려준다.
        Map<String, Object> result = sqlSession.selectOne("users.userInfoByEmail", this.reqData);

        if (result != null) {
            String userNo = String.valueOf(result.get("USERNO"));

            // helper.
            this.apiResult.addProperty("resultCode", "200");
            this.apiResult.addProperty("message", "Success");
            this.apiResult.addProperty("userNo", userNo);
        } else {
            // 데이터 없음.
            this.apiResult.addProperty("resultCode", "404");
        }
    }
}