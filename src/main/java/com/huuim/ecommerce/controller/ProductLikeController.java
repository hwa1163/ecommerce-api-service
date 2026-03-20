package com.huuim.ecommerce.controller;

import com.huuim.ecommerce.common.auth.AuthService;
import com.huuim.ecommerce.domain.user.User;
import com.huuim.ecommerce.dto.like.ProductLikeResponse;
import com.huuim.ecommerce.service.ProductLikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 상품 좋아요 API 컨트롤러
 * POST /api/v1/products/{productId}/likes
 */
@RestController
@RequestMapping("/api/v1/products/{productId}/likes")
public class ProductLikeController {

    private final ProductLikeService productLikeService;
    private final AuthService authService;

    public ProductLikeController(ProductLikeService productLikeService,
                                 AuthService authService) {
        this.productLikeService = productLikeService;
        this.authService = authService;
    }

    /**
     * 상품 좋아요 등록
     * 1. 헤더로 사용자 인증
     * 2. 상품 좋아요 서비스 호출
     * 3. 중복 좋아요면 멱등하게 그대로 응답
     */
    @PostMapping
    public ResponseEntity<ProductLikeResponse> likeProduct(
            @PathVariable Long productId,
            @RequestHeader(value = AuthService.LOGIN_ID_HEADER, required = false) String loginId,
            @RequestHeader(value = AuthService.LOGIN_PW_HEADER, required = false) String loginPw
    ) {
        User user = authService.authenticate(loginId, loginPw);

        ProductLikeResponse response = productLikeService.likeProduct(productId, user);

        //새로 좋아요 생성되었든, 이미 좋아요 상태였든 정상 응답
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 상품 좋아요 취소
     * 이미 좋아요가 없는 상태여도 정상 응답 처리
     */
    @DeleteMapping
    public ResponseEntity<ProductLikeResponse> unlikeProduct(
            @PathVariable Long productId,
            @RequestHeader(value = AuthService.LOGIN_ID_HEADER, required = false) String loginId,
            @RequestHeader(value = AuthService.LOGIN_PW_HEADER, required = false) String loginPw
    ) {
        User user = authService.authenticate(loginId, loginPw);
        ProductLikeResponse response = productLikeService.unlikeProduct(productId, user);
        return ResponseEntity.ok(response);
    }
}