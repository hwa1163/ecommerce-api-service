package com.huuim.ecommerce.controller;

import com.huuim.ecommerce.common.auth.AuthService;
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
    // private static final String LOGIN_ID_HEADER = "X-Huuim-LoginId";
    // private static final String LOGIN_PW_HEADER = "X-Huuim-LoginPw";

    // private final ProductService productService;

    // public ProductController(ProductService productService) {
    //     this.productService = productService;
    // }
    
    // AuthService 에서 가져오기때문에 이제 필요하지 않아짐 

    private final ProductService productService;
    private final AuthService authService;

    public ProductController(ProductService productService, AuthService authService) {
        this.productService = productService;
        this.authService = authService;
    }

    /**
     * 상품 등록
     * 명세에 맞는 헤더 존재 여부만 체크
     * POST /api/v1/products
     */
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @RequestHeader(value = AuthService.LOGIN_ID_HEADER, required = false) String loginId,
            @RequestHeader(value = AuthService.LOGIN_PW_HEADER, required = false) String loginPw,
            @RequestBody ProductCreateRequest request
    ) {
        /**
         * 최소한 헤더 존재 여부는 체크.
         */
        authService.authenticate(loginId, loginPw);

        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 상품 목록 조회
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

   
}