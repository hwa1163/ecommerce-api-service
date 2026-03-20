package com.huuim.ecommerce.dto.product;

import com.huuim.ecommerce.domain.Product;
import java.time.LocalDateTime;

/**
 * 상품 단건 응답 DTO
 */
public class ProductResponse {

    private Long id;
    private String name;
    private String brand;
    private Integer price;
    private Integer stock;
    private Long likeCount;
    private LocalDateTime createdAt;

    public ProductResponse(Long id, String name, String brand, Integer price, Integer stock, Long likeCount, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.stock = stock;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
    }

    //엔티티 -> DTO 변환 정적 메서드
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getPrice(),
                product.getStock(),
                product.getLikeCount(),
                product.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
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

    public Long getLikeCount() {
        return likeCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}