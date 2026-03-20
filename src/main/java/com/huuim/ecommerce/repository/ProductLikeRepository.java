package com.huuim.ecommerce.repository;

import com.huuim.ecommerce.domain.like.ProductLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 상품 좋아요 Repository
 */
public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {

    //특정 사용자와 특정 상품 조합의 좋아요 존재 여부 확인
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    //특정 사용자와 특정 상품 조합의 좋아요 조회
    Optional<ProductLike> findByUserIdAndProductId(Long userId, Long productId);
}