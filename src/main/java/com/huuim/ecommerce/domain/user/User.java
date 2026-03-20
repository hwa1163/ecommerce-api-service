package com.huuim.ecommerce.domain.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/*
//사용자 엔티티
 *
//- 회원가입
//- 헤더 기반 사용자 식별
*/

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
            
            //로그인 ID는 중복될 수 없도록 유니크
            
            @UniqueConstraint(name = "uk_users_login_id", columnNames = "login_id")
        }
)
public class User {

    
    //사용자 PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //로그인 ID
    @Column(name = "login_id", nullable = false, length = 50)
    private String loginId;
    
    //로그인 비밀번호
    @Column(name = "login_pw", nullable = false, length = 100)
    private String loginPw;

    //사용자 이름
    @Column(nullable = false, length = 50)
    private String name;
    
    //생성일시
    @Column(nullable = false)
    private LocalDateTime createdAt;

    
    //수정일시
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    //JPA 기본 생성자
    protected User() {
    }
    
    //회원가입 생성자
    public User(String loginId, String loginPw, String name) {
        this.loginId = loginId;
        this.loginPw = loginPw;
        this.name = name;
    }

    
    //최초 저장 직전 자동 실행
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    //수정 직전 자동 실행
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    //비밀번호 변경
    //이후 "비밀번호 변경" API 추가 시 재사용 가능
    public void changePassword(String newPassword) {
        this.loginPw = newPassword;
    }

    public Long getId() {
        return id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}