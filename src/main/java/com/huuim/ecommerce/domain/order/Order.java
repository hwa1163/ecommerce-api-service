package com.huuim.ecommerce.domain.order;

import com.huuim.ecommerce.domain.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 주문 엔티티
 * 주문 1건의 대표 정보를 저장한다.
 * 실제 주문 상품 목록은 OrderItem으로 분리
 */
@Entity
@Table(name = "orders")
public class Order {

    //주문 PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //주문한 사용자
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 주문 총 금액
     * 주문 생성 시 OrderItem들의 합계를 계산해서 저장
     */
    @Column(nullable = false)
    private Long totalPrice;

    // 주문 생성일시
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * 주문 상세 항목 목록
     * cascade = ALL:
     * Order 저장 시 OrderItem도 함께 저장
     * orphanRemoval = true: (캐스케이드 D)
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    protected Order() {
    }

    public Order(User user) {
        this.user = user;
        this.totalPrice = 0L;
    }

    /**
     * 주문 항목 추가
     * totalPrice도 함께 누적.
     */
    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.assignOrder(this);
        this.totalPrice += orderItem.getOrderPrice();
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();

        if (this.totalPrice == null) {
            this.totalPrice = 0L;
        }
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Long getTotalPrice() {
        return totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
}
