package com.huuim.ecommerce.dto.user;

import com.huuim.ecommerce.domain.user.User;
import java.time.LocalDateTime;

/**
 * 사용자 응답 DTO
 * 보안상 비밀번호는 포함 x.
 */
public class UserResponse {

    private Long id;
    private String loginId;
    private String name;
    private LocalDateTime createdAt;

    public UserResponse(Long id, String loginId, String name, LocalDateTime createdAt) {
        this.id = id;
        this.loginId = loginId;
        this.name = name;
        this.createdAt = createdAt;
    }

    //엔티티 -> DTO 변환
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getLoginId(),
                user.getName(),
                user.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}