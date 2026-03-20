package com.huuim.ecommerce.dto.product;

import java.util.List;


//상품 목록 조회 응답 DTO
public class ProductListResponse {

    //현재 페이지 번호
    private int page;

    //페이지 크기
    private int size;

    //전체 페이지 수
    private int totalPages;

    //전체 데이터 수
    private long totalElements;

    //현재 페이지 상품 목록
    private List<ProductResponse> products;

    public ProductListResponse(int page, int size, int totalPages, long totalElements, List<ProductResponse> products) {
        this.page = page;
        this.size = size;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.products = products;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public List<ProductResponse> getProducts() {
        return products;
    }
}