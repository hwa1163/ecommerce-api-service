package com.huuim.ecommerce.domain.like;

import com.huuim.ecommerce.domain.product.Product;
import com.huuim.ecommerce.domain.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 상품 좋아요 엔티티
 * 1. 같은 사용자가 같은 상품에 중복 좋아요를 못 하게 하기
 * 2. 좋아요한 이력을 남기기
 */
@Entity
@Table(
        name = "product_likes",
        uniqueConstraints = {
            //유저, 상품 unique 
            @UniqueConstraint(
                    name = "uk_product_likes_user_id_product_id",
                    columnNames = {"user_id", "product_id"}
            )
        }
)
public class ProductLike {

    //좋아요 PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //좋아요를 누른 사용자
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 좋아요 대상 상품
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    //생성일시
    @Column(nullable = false)
    private LocalDateTime createdAt;

    //JPA 기본 생성자
    protected ProductLike() {
    }

    //좋아요 생성자
    public ProductLike(User user, Product product) {
        this.user = user;
        this.product = product;
    }

    //최초 저장 시각 자동 세팅
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Product getProduct() {
        return product;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
