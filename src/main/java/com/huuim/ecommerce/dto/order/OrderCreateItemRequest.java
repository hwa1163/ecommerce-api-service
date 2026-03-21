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

     //테스트 코드나 JSON 바인딩 시 값을 넣기 위한 setter
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    //테스트 코드나 JSON 바인딩 시 값을 넣기 위한 setter
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}