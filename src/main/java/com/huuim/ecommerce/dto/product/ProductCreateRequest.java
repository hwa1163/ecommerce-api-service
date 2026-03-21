package com.huuim.ecommerce.dto.product;

/**
 * 상품 등록 요청 DTO
 */
public class ProductCreateRequest {

    //상품명
    private String name;

    //브랜드명
    private String brand;

    //가격
    private Integer price;

    //재고
    private Integer stock;

    public ProductCreateRequest() {
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public Integer getPrice() {
        return price;
    }

    public Integer getStock() {
        return stock;
    }

    //테스트 코드와 JSON 바인딩을 위한 setter
    public void setName(String name) {
        this.name = name;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

}