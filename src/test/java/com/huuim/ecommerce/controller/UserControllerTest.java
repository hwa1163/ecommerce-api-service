package com.huuim.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huuim.ecommerce.dto.user.UserCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 사용자 API 테스트
 *
 * 검증 대상:
 * - 회원가입 성공
 * - 중복 loginId 회원가입 실패
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공")
    void createUser_success() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest();
        request.setLoginId("huuim");
        request.setLoginPw("huuim1114");
        request.setName("김민회");

        // when & then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.loginId").value("huuim"))
                .andExpect(jsonPath("$.name").value("김민회"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("중복 loginId 회원가입 시 400 반환")
    void createUser_fail_whenDuplicateLoginId() throws Exception {
        // given
        UserCreateRequest request1 = new UserCreateRequest();
        request1.setLoginId("huuim");
        request1.setLoginPw("huuim1114");
        request1.setName("김민회");

        UserCreateRequest request2 = new UserCreateRequest();
        request2.setLoginId("huuim");
        request2.setLoginPw("5678");
        request2.setName("최미래");

        // 먼저 동일 loginId로 한 번 회원가입 성공시키기
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        // when & then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 사용 중인 loginId입니다."));
    }
}