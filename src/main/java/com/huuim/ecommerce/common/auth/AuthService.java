package com.huuim.ecommerce.common.auth;

import com.huuim.ecommerce.domain.user.User;
import com.huuim.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 헤더 기반 사용자 검증 서비스
 * 정식 인증/인가 대신 아래 헤더로 사용자를 식별한다.
 * - X-Huuim-LoginId
 * - X-Huuim-LoginPw
 * 이 서비스를 통해 헤더값으로 사용자를 조회하고 검증
 */
@Service
@Transactional(readOnly = true)
public class AuthService {

    //컨트롤러에서 재사용하기 쉽게 public static final 로 선언
    public static final String LOGIN_ID_HEADER = "X-Huuim-LoginId";
    public static final String LOGIN_PW_HEADER = "X-Huuim-LoginPw";

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 헤더값으로 사용자 인증
     *
     * 동작 방식:
     * 1. 헤더 누락 여부 확인
     * 2. loginId + loginPw로 사용자 조회
     * 3. 없으면 인증 실패 처리
     * 4. 있으면 해당 사용자 반환
     *
     * 이후 Product/Like/Order에서
     * "현재 요청 사용자"를 얻는 공통 진입점 역할을 한다.
     */
    public User authenticate(String loginId, String loginPw) {
        validateHeaders(loginId, loginPw);

        return userRepository.findByLoginIdAndLoginPw(loginId, loginPw)
                .orElseThrow(() -> new IllegalArgumentException("로그인 헤더 정보가 올바르지 않습니다."));
    }

    /**
     * 헤더 존재 여부 검증
     */
    private void validateHeaders(String loginId, String loginPw) {
        if (loginId == null || loginId.isBlank()) {
            throw new IllegalArgumentException("X-Huuim-LoginId 헤더는 필수입니다.");
        }

        if (loginPw == null || loginPw.isBlank()) {
            throw new IllegalArgumentException("X-Huuim-LoginPw 헤더는 필수입니다.");
        }
    }
}
