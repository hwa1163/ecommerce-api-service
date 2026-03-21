package com.huuim.ecommerce.domain.order;

import com.huuim.ecommerce.domain.product.Product;
import jakarta.persistence.*;

/**
 * 주문 상세 항목 엔티티
 * 주문 안에 어떤 상품이 몇 개, 얼마에 주문되었는지 저장
 */
@Entity
@Table(name = "order_items")
public class OrderItem {

    // 주문 상세 PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 소속 주문
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    //주문 대상 상품
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 주문 수량
    @Column(nullable = false)
    private Integer quantity;

    /**
     * 주문 시점의 상품 단가
     * 나중에 상품 가격이 바뀌더라도 당시 주문 가격을 보존하기 위해 저장
     */
    @Column(nullable = false)
    private Integer unitPrice;

    // 주문 항목 총 금액 unitPrice * quantity
    @Column(nullable = false)
    private Long orderPrice;

    protected OrderItem() {
    }

    public OrderItem(Product product, Integer quantity, Integer unitPrice) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.orderPrice = (long) unitPrice * quantity;
    }

    //주문과의 연관관계 세팅
    public void assignOrder(Order order) {
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public Product getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getUnitPrice() {
        return unitPrice;
    }

    public Long getOrderPrice() {
        return orderPrice;
    }
}