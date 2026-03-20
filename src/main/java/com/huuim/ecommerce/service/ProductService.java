package com.huuim.ecommerce.service;

import com.huuim.ecommerce.dto.product.ProductListResponse;
import com.huuim.ecommerce.domain.product.Product;
import com.huuim.ecommerce.dto.product.ProductCreateRequest;
import com.huuim.ecommerce.dto.product.ProductResponse;
import com.huuim.ecommerce.repository.ProductRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 상품 서비스
 * - 상품 등록
 * - 상품 목록 조회
 * - 정렬 기준 해석
 * - 페이징 처리
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 상품 등록
     * Controller에서 헤더 존재 여부를 확인한 뒤 호출.
     */
    public ProductResponse createProduct(ProductCreateRequest request) {
        validateCreateRequest(request);

        Product product = new Product(
                request.getName(),
                request.getBrand(),
                request.getPrice(),
                request.getStock()
        );

        Product savedProduct = productRepository.save(product);
        return ProductResponse.from(savedProduct);
    }

    /**
     * 상품 목록 조회
     * - sort: latest / price_asc / likes_desc
     * - page: 기본값 0
     * - size: 기본값 20
     */
    public ProductListResponse getProducts(String sort, int page, int size) {
        
        Pageable pageable = PageRequest.of(page, size, resolveSort(sort));
        Page<Product> productPage = productRepository.findAll(pageable);

        List<ProductResponse> productResponses = productPage.getContent()
                .stream()
                .map(ProductResponse::from)
                .toList();

        return new ProductListResponse(
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalPages(),
                productPage.getTotalElements(),
                productResponses
        );
        
    }

    /**
     * latest     -> createdAt 내림차순
     * price_asc  -> price 오름차순
     * likes_desc -> likeCount 내림차순
     */
    private Sort resolveSort(String sort) {
        if (sort == null || sort.isBlank() || sort.equals("latest")) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        return switch (sort) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "likes_desc" -> Sort.by(Sort.Direction.DESC, "likeCount");
            default -> throw new IllegalArgumentException("지원하지 않는 정렬 조건입니다. sort = " + sort);
            
        };
    }

    /**
     * 상품 등록 요청값 검증
     */
    private void validateCreateRequest(ProductCreateRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("상품명은 필수입니다.");
        }

        if (request.getBrand() == null || request.getBrand().isBlank()) {
            throw new IllegalArgumentException("브랜드명은 필수입니다.");
        }

        if (request.getPrice() == null || request.getPrice() < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }

        if (request.getStock() == null || request.getStock() < 0) {
            throw new IllegalArgumentException("재고는 0 이상이어야 합니다.");
        }
    }
}