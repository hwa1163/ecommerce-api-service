package com.huuim.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.huuim.ecommerce.domain.product.Product;

/**
 * 상품 Repository
 */

public interface ProductRepository extends JpaRepository<Product, Long> {
    
}