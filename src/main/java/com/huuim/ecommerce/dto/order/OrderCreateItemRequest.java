package com.huuim.ecommerce.dto.order;

//주문 요청 안의 개별 상품 항목 DTO
public class OrderCreateItemRequest {

    //상품 ID
    private Long productId;

    //주문 수량
    private Integer quantity;

    public OrderCreateItemRequest() {
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}