package com.huuim.ecommerce.service;

import com.huuim.ecommerce.domain.like.ProductLike;
import com.huuim.ecommerce.domain.product.Product;
import com.huuim.ecommerce.domain.user.User;
import com.huuim.ecommerce.dto.like.ProductLikeResponse;
import com.huuim.ecommerce.repository.ProductLikeRepository;
import com.huuim.ecommerce.repository.ProductRepository;

import jakarta.persistence.OptimisticLockException;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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
    @CacheEvict(value = "productList", allEntries = true)
    @Transactional
    public ProductLikeResponse likeProduct(Long productId, User user) {

        try {
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
                        true,
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
        } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
            throw new IllegalStateException("동시에 여러 좋아요 요청이 처리되어 충돌이 발생했습니다. 다시 시도해주세요.");
        }
    }

     /**
     * 상품 좋아요 취소
     * - 좋아요 상태면 like 엔티티 삭제 + likeCount 감소
     * - 이미 좋아요하지 않은 상태면 아무것도 하지 않고 그대로
     */
    @CacheEvict(value = "productList", allEntries = true)
    @Transactional
    public ProductLikeResponse unlikeProduct(Long productId, User user) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다. productId = " + productId));

            ProductLike productLike = productLikeRepository.findByUserIdAndProductId(user.getId(), productId)
                    .orElse(null);

            //이미 좋아요하지 않은 상태라면 에러 대신 정상 응답을 반환한다.
            if (productLike == null) {
                return new ProductLikeResponse(
                        product.getId(),
                        product.getLikeCount(),
                        false,
                        "이미 좋아요가 취소된 상태입니다."
                );
            }

            //좋아요 이력 삭제
            productLikeRepository.delete(productLike);

            // 상품 좋아요 수 감소
            product.decreaseLikeCount();

            return new ProductLikeResponse(
                    product.getId(),
                    product.getLikeCount(),
                    false,
                    "좋아요가 취소되었습니다."
            );
        } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
            throw new IllegalStateException("동시에 여러 좋아요 취소 요청이 처리되어 충돌이 발생했습니다. 다시 시도해주세요.");
        }
    }
}