package com.huuim.ecommerce.service;

import com.huuim.ecommerce.domain.user.User;
import com.huuim.ecommerce.dto.user.UserCreateRequest;
import com.huuim.ecommerce.dto.user.UserResponse;
import com.huuim.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 서비스
 */
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 회원가입
     * - loginId 필수
     * - loginPw 필수
     * - name 필수
     * - loginId 중복 불가
     */

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        validateCreateRequest(request);
        validateDuplicateLoginId(request.getLoginId());

        User user = new User(
                request.getLoginId(),
                request.getLoginPw(),
                request.getName()
        );

        User savedUser = userRepository.save(user);
        return UserResponse.from(savedUser);
    }

    //회원가입 요청값 검증
    private void validateCreateRequest(UserCreateRequest request) {
        if (request.getLoginId() == null || request.getLoginId().isBlank()) {
            throw new IllegalArgumentException("loginId는 필수입니다.");
        }

        if (request.getLoginPw() == null || request.getLoginPw().isBlank()) {
            throw new IllegalArgumentException("loginPw는 필수입니다.");
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("name은 필수입니다.");
        }
    }

    // 로그인 ID 중복 검증
    private void validateDuplicateLoginId(String loginId) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new IllegalArgumentException("이미 사용 중인 loginId입니다.");
        }
    }
}