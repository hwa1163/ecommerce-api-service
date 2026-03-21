package com.huuim.ecommerce.dto.order;

import java.util.List;

/**
 * 주문 생성 요청 DTO 
 * 여러 상품을 한 번에 주문할 수 있도록 items 리스트를 받는다.
 */
public class OrderCreateRequest {

    //주문 항목 목록
    private List<OrderCreateItemRequest> items;

    public OrderCreateRequest() {
    }

    public List<OrderCreateItemRequest> getItems() {
        return items;
    }

    //테스트 코드나 JSON 바인딩 시 값을 넣기 위한 setter
    public void setItems(List<OrderCreateItemRequest> items) {
        this.items = items;
    }
}