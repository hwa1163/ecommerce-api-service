package com.huuim.ecommerce.repository;

import com.huuim.ecommerce.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 상품 Repository
 */

public interface ProductRepository extends JpaRepository<Product, Long> {
    
}