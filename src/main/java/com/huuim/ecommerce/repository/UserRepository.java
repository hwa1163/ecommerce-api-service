package com.huuim.ecommerce.repository;

import com.huuim.ecommerce.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 사용자 Repository
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 로그인 ID로 사용자 조회
     * 회원가입 중복 체크, 로그인 헤더 기반 사용자 식별에 사용
     */
    Optional<User> findByLoginId(String loginId);

    //로그인 ID + 비밀번호로 사용자 조회
    Optional<User> findByLoginIdAndLoginPw(String loginId, String loginPw);

    //로그인 ID 중복 여부 확인
    boolean existsByLoginId(String loginId);
}