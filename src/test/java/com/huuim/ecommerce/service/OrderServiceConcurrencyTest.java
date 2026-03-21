package com.huuim.ecommerce.service;

import com.huuim.ecommerce.domain.product.Product;
import com.huuim.ecommerce.domain.user.User;
import com.huuim.ecommerce.dto.order.OrderCreateItemRequest;
import com.huuim.ecommerce.dto.order.OrderCreateRequest;
import com.huuim.ecommerce.repository.OrderRepository;
import com.huuim.ecommerce.repository.ProductRepository;
import com.huuim.ecommerce.repository.UserRepository;
import com.huuim.ecommerce.repository.OrderItemRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 주문 동시성 테스트
 * - 같은 상품에 대해 동시에 주문 요청이 들어왔을 때
 *   재고가 음수가 되지 않는지 검증한다.

 *  멀티 스레드를 사용하므로 테스트 메서드에 @Transactional을 붙이지 않음
 */
@SpringBootTest
class OrderServiceConcurrencyTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    
    @AfterEach
    void tearDown() {
        orderItemRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    //테스트용 사용자 생성
    private User createUser(String loginId) {
        return userRepository.save(new User(loginId, "1234", loginId));
    }

    @Test
    @DisplayName("동시에 같은 상품을 주문해도 재고보다 많은 주문은 성공하지 않는다")
    void createOrder_concurrently() throws Exception {
        // given
        User user1 = createUser("user25");
        User user2 = createUser("user26");
        User user3 = createUser("user27");
        User user4 = createUser("user28");
        User user5 = createUser("user29");

        /**
         * 재고 3개짜리 상품 생성
         *
         * 각 요청은 1개씩 주문하도록 설정
         * 동시에 5명이 주문하면 최대 3명만 성공해야 함
         */
        Product product = productRepository.saveAndFlush(
            new Product("로델리아 헤어 전문가 픽 패키지 세트", "huuim", 108000, 3)
        );

        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        List<User> users = List.of(user1, user2, user3, user4, user5);

        // when
        for (User user : users) {
            executorService.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();

                    OrderCreateItemRequest item = new OrderCreateItemRequest();
                    item.setProductId(product.getId());
                    item.setQuantity(1);

                    OrderCreateRequest request = new OrderCreateRequest();
                    request.setItems(List.of(item));

                    orderService.createOrder(user, request);
                    successCount.incrementAndGet();

                } catch (Exception e) {
                    //재고 부족 등의 이유로 실패한 경우
                    System.out.println("주문 실패: " + e.getClass().getName() + " / " + e.getMessage());
                    failCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // 모든 스레드가 준비될 때까지 기다렸다가 한 번에 시작시켜 동시성을 유도
        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();

        executorService.shutdown();

        // then
        Product foundProduct = productRepository.findById(product.getId()).orElseThrow();

        //재고가 3개였으므로 최대 3건만 성공해야 함
        assertThat(successCount.get()).isEqualTo(3);
        assertThat(failCount.get()).isEqualTo(2);

        // 최종 재고는 0이어야 하고 음수가 되면 안됨
        assertThat(foundProduct.getStock()).isEqualTo(0);

        //실제 주문 건수도 성공한 주문 수와 일치해야 함
        assertThat(orderRepository.count()).isEqualTo(3);
    }

}