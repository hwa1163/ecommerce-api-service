package com.huuim.ecommerce.controller;

import com.huuim.ecommerce.dto.user.UserCreateRequest;
import com.huuim.ecommerce.dto.user.UserResponse;
import com.huuim.ecommerce.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 API 컨트롤러
 * POST /api/v1/users
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 회원가입
     * POST /api/v1/users
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserCreateRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}