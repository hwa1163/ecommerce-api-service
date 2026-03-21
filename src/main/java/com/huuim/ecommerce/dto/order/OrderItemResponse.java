package com.huuim.ecommerce.dto.order;

import com.huuim.ecommerce.domain.order.OrderItem;

//주문 상세 응답 DTO
public class OrderItemResponse {

    private Long productId;
    private String productName;
    private Integer quantity;
    private Integer unitPrice;
    private Long orderPrice;

    public OrderItemResponse(Long productId, String productName, Integer quantity, Integer unitPrice, Long orderPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.orderPrice = orderPrice;
    }

    public static OrderItemResponse from(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getQuantity(),
                orderItem.getUnitPrice(),
                orderItem.getOrderPrice()
        );
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
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