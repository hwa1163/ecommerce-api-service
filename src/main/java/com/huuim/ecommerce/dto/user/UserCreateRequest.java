package com.huuim.ecommerce.dto.user;


//회원가입 요청 DTO
public class UserCreateRequest {

    //로그인 ID
    private String loginId;
    
    //로그인 비밀번호
    private String loginPw;

    //사용자 이름
    private String name;

    public UserCreateRequest() {
    }

    public String getLoginId() {
        return loginId;
    }

    public String getLoginPw() {
        return loginPw;
    }

    public String getName() {
        return name;

    }
    //테스트 코드와 JSON 바인딩을 위한 setter
    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public void setLoginPw(String loginPw) {
        this.loginPw = loginPw;
    }

    public void setName(String name) {
        this.name = name;
    }

}