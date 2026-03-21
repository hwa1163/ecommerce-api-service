package com.huuim.ecommerce.repository;

import com.huuim.ecommerce.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

//주문 Repository
public interface OrderRepository extends JpaRepository<Order, Long>{

}