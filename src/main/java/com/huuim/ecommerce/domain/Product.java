package com.huuim.ecommerce.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/*/
 * 상품 엔티티
 *
 * 요구사항상 상품 목록 조회 시
 * - 최신순 정렬
 * - 가격 오름차순 정렬
 * - 좋아요 많은 순 정렬
*/
@Entity
@Table(name = "products")
public class Product {

    //상품 PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //상품명
    @Column(nullable = false, length = 100)
    private String name;

    //브랜드명
    @Column(nullable = false, length = 100)
    private String brand;

    // 가격
    @Column(nullable = false)
    private Integer price;
    
    //재고 수량
    @Column(nullable = false)
    private Integer stock;

    //좋아요 수
    //이후 좋아요 등록/취소 시 이 값을 함께 증감
    @Column(nullable = false)
    private Long likeCount;

    // 생성일시
    //latest 정렬 기준으로 사용
    @Column(nullable = false)
    private LocalDateTime createdAt;

    
    //수정일시
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    
    // JPA 기본 생성자
    protected Product() {

    }
    
    //상품 생성용 생성자
    public Product(String name, String brand, Integer price, Integer stock) {
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.stock = stock;
        this.likeCount = 0L;
    }
    
    //엔티티 최초 저장 전에 자동 실행
    //createdAt / updatedAt 초기화
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        //null  방지
        if (this.likeCount == null) {
            this.likeCount = 0L;
        }
    }
    
    //엔티티 수정 전에 자동 실행
    //updatedAt 갱신
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    //상품 정보 수정 메서드
    public void updateInfo(String name, String brand, Integer price, Integer stock) {
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.stock = stock;
    }

    //좋아요 수 증가
    public void increaseLikeCount() {
        this.likeCount += 1;
    }

    //좋아요 수 감소    
    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount -= 1;
        }
    }
    
    //주문 시 재고 차감용 메서드
    public void decreaseStock(int quantity) {
        if (this.stock < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.stock -= quantity;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}