package com.huuim.ecommerce.service;

import com.huuim.ecommerce.domain.like.ProductLike;
import com.huuim.ecommerce.domain.product.Product;
import com.huuim.ecommerce.domain.user.User;
import com.huuim.ecommerce.dto.like.ProductLikeResponse;
import com.huuim.ecommerce.repository.ProductLikeRepository;
import com.huuim.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 상품 좋아요 서비스
 * 1. 특정 사용자의 특정 상품 좋아요 등록
 * 2. 중복 좋아요 방지
 * 3. 상품 likeCount 증가
 */
@Service
@Transactional(readOnly = true)
public class ProductLikeService {

    private final ProductLikeRepository productLikeRepository;
    private final ProductRepository productRepository;

    public ProductLikeService(ProductLikeRepository productLikeRepository,
                              ProductRepository productRepository) {
        this.productLikeRepository = productLikeRepository;
        this.productRepository = productRepository;
    }

    /**
     * 상품 좋아요 등록
     * 같은 사용자가 같은 상품에 이미 좋아요한 경우: 그대로 성공처럼 응답
     * 
     * 아직 좋아요하지 않은 경우: ProductLike를 저장하고 Product.likeCount를 1 증가
     */
    @Transactional
    public ProductLikeResponse likeProduct(Long productId, User user) {
        /**
         * 좋아요 대상 상품 조회
         * 없으면 예외 발생
         */
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다. productId = " + productId));

        /**
         * 이미 좋아요한 상태인지 확인
         */
        boolean alreadyLiked = productLikeRepository.existsByUserIdAndProductId(user.getId(), productId);

        if (alreadyLiked) {
            return new ProductLikeResponse(
                    product.getId(),
                    product.getLikeCount(),
                    false,
                    "이미 좋아요한 상품입니다."
            );
        }

        //좋아요 엔티티 생성 및 저장
        ProductLike productLike = new ProductLike(user, product);
        productLikeRepository.save(productLike);

        //상품의 좋아요 수 증가
        product.increaseLikeCount();

        return new ProductLikeResponse(
                product.getId(),
                product.getLikeCount(),
                true,
                "좋아요가 등록되었습니다."
        );
    }
}