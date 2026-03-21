package com.huuim.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.huuim.ecommerce.domain.product.Product;

import jakarta.persistence.LockModeType;

/**
 * 상품 Repository
 */

public interface ProductRepository extends JpaRepository<Product, Long> {
    /**
     * 주문 시 재고 차감을 안전하게 처리하기 위해
     * 비관적 쓰기 락을 걸고 상품을 조회
     *
     * 같은 상품에 대한 동시 주문이 들어왔을 때 한 트랜잭션이 끝날 때까지 다른 트랜잭션이 기다리도록
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :productId")
    Optional<Product> findByIdForUpdate(Long productId);
}