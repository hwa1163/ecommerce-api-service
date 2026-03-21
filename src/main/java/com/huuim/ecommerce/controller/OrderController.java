package com.huuim.ecommerce.controller;

import com.huuim.ecommerce.common.auth.AuthService;
import com.huuim.ecommerce.domain.user.User;
import com.huuim.ecommerce.dto.order.OrderCreateRequest;
import com.huuim.ecommerce.dto.order.OrderResponse;
import com.huuim.ecommerce.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 주문 API 컨트롤러
 *
 * 명세:
 * POST /api/v1/orders 
 * 인증 필요
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final AuthService authService;

    public OrderController(OrderService orderService,
                           AuthService authService) {
        this.orderService = orderService;
        this.authService = authService;
    }

    // 주문 요청 헤더로 사용자 인증 후 주문 생성
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader(value = AuthService.LOGIN_ID_HEADER, required = false) String loginId,
            @RequestHeader(value = AuthService.LOGIN_PW_HEADER, required = false) String loginPw,
            @RequestBody OrderCreateRequest request
    ) {
        User user = authService.authenticate(loginId, loginPw);
        OrderResponse response = orderService.createOrder(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}