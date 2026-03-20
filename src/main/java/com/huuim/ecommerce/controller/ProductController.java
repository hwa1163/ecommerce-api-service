package com.huuim.ecommerce.controller;

import com.huuim.ecommerce.dto.product.ProductCreateRequest;
import com.huuim.ecommerce.dto.product.ProductListResponse;
import com.huuim.ecommerce.dto.product.ProductResponse;
import com.huuim.ecommerce.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 상품 API 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    /**
     * 사용자 식별용헤더
     */
    private static final String LOGIN_ID_HEADER = "X-Huuim-LoginId";
    private static final String LOGIN_PW_HEADER = "X-Huuim-LoginPw";

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 상품 등록
     *
     * 현재 단계에서는 실제 인증/인가 구현 대신
     * 명세에 맞는 헤더 존재 여부만 체크한다.
     *
     * POST /api/v1/products
     */
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @RequestHeader(value = LOGIN_ID_HEADER, required = false) String loginId,
            @RequestHeader(value = LOGIN_PW_HEADER, required = false) String loginPw,
            @RequestBody ProductCreateRequest request
    ) {
        /**
         * 인증이 주요 스코프는 아니라고 했지만,
         * "인증 필요" 엔드포인트이므로 최소한 헤더 존재 여부는 체크한다.
         */
        validateAuthHeaders(loginId, loginPw);

        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 상품 목록 조회
     *
     * GET /api/v1/products?sort=latest&page=0&size=20
     */
    @GetMapping
    public ResponseEntity<ProductListResponse> getProducts(
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        ProductListResponse response = productService.getProducts(sort, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * 인증 헤더 존재 여부 검증
     *
     * 나중에 User 도메인과 붙이면
     * 여기서 진짜 로그인 사용자 검증 로직으로 바꾸면 된다.
     */
    private void validateAuthHeaders(String loginId, String loginPw) {
        if (loginId == null || loginId.isBlank()) {
            throw new IllegalArgumentException("X-Huuim-LoginId 헤더는 필수입니다.");
        }

        if (loginPw == null || loginPw.isBlank()) {
            throw new IllegalArgumentException("X-Huuim-LoginPw 헤더는 필수입니다.");
        }
    }
}