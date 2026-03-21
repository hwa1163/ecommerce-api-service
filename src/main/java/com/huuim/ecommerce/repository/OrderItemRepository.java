package com.huuim.ecommerce.repository;


import com.huuim.ecommerce.domain.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 주문 상세 Repository
 * 동시성 테스트나 테스트 데이터 정리 시 order_items 테이블을 직접 비우기 위해 사용
 */

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}