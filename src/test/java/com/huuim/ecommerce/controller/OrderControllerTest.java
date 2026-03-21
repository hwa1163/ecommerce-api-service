package com.huuim.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huuim.ecommerce.domain.product.Product;
import com.huuim.ecommerce.domain.user.User;
import com.huuim.ecommerce.dto.order.OrderCreateItemRequest;
import com.huuim.ecommerce.dto.order.OrderCreateRequest;
import com.huuim.ecommerce.repository.ProductRepository;
import com.huuim.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Order API 예외 케이스 테스트
 *
 * 테스트 대상:
 * - 존재하지 않는 상품 주문
 * - 재고 부족
 * - 중복 상품 요청
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    //JSON 변환용 객체
    @Autowired
    private ObjectMapper objectMapper;

    // 테스트용 사용자 생성
    private User createUser() {
        return userRepository.save(
                new User("huuim", "huuim1114", "김민회")
        );
    }

    //테스트용 상품 생성
    private Product createProduct(String name, int price, int stock) {
        return productRepository.save(
            new Product(name, "huuim", price, stock)
    );
    }

    // 1.존재하지 않는 상품 주문 테스트

    @Test
    @DisplayName("존재하지 않는 상품 주문 시 400 반환")
    void order_fail_whenProductNotFound() throws Exception {

        // given
        User user = createUser();

        OrderCreateItemRequest item = new OrderCreateItemRequest();
        // 존재하지 않는 productId
        item.setProductId(9999L);
        item.setQuantity(1);

        OrderCreateRequest request = new OrderCreateRequest();
        request.setItems(List.of(item));

        // when & then
        mockMvc.perform(post("/api/v1/orders")
                        .header("X-Huuim-LoginId", user.getLoginId())
                        .header("X-Huuim-LoginPw", user.getLoginPw())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // 2.재고 부족 테스트
    @Test
    @DisplayName("재고보다 많은 수량 주문 시 400 반환")
    void order_fail_whenOutOfStock() throws Exception {

        // given
        User user = createUser();

        // 재고 1개짜리 상품
        Product product = createProduct("웰컴 투 휴이엠 모이스처 밤 100ml", 20000, 1);

        OrderCreateItemRequest item = new OrderCreateItemRequest();
        item.setProductId(product.getId());
        // 재고보다 많은 수량 요청
        item.setQuantity(5);

        OrderCreateRequest request = new OrderCreateRequest();
        request.setItems(List.of(item));

        // when & then
        mockMvc.perform(post("/api/v1/orders")
                        .header("X-Huuim-LoginId", user.getLoginId())
                        .header("X-Huuim-LoginPw", user.getLoginPw())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // 3. 동일 상품 중복 요청 테스트
    @Test
    @DisplayName("하나의 주문에 동일 상품이 중복되면 400 반환")
    void order_fail_whenDuplicateProduct() throws Exception {

        // given
        User user = createUser();

        Product product = createProduct("웰컴 투 휴이엠 모이스처 밤 100ml", 24000, 10);

        // 같은 상품 2번 넣음
        OrderCreateItemRequest item1 = new OrderCreateItemRequest();
        item1.setProductId(product.getId());
        item1.setQuantity(1);

        OrderCreateItemRequest item2 = new OrderCreateItemRequest();
        item2.setProductId(product.getId());
        item2.setQuantity(2);

        OrderCreateRequest request = new OrderCreateRequest();
        request.setItems(List.of(item1, item2));

        // when & then
        mockMvc.perform(post("/api/v1/orders")
                        .header("X-Huuim-LoginId", user.getLoginId())
                        .header("X-Huuim-LoginPw", user.getLoginPw())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("여러 상품 주문 성공 및 재고 차감 검증")
    void order_success() throws Exception {

        // given
        User user = createUser();

        // 상품 2개 생성
        Product product1 = createProduct("웰컴 투 휴이엠 모이스처 밤 100ml", 24000, 10); // 재고 10
        Product product2 = createProduct("웰컴 투 휴이엠 샴푸 1000ml", 36000, 5);  // 재고 5

        // 주문 요청 구성
        OrderCreateItemRequest item1 = new OrderCreateItemRequest();
        item1.setProductId(product1.getId());
        item1.setQuantity(2); // 2개 주문

        OrderCreateItemRequest item2 = new OrderCreateItemRequest();
        item2.setProductId(product2.getId());
        item2.setQuantity(1); // 1개 주문

        OrderCreateRequest request = new OrderCreateRequest();
        request.setItems(List.of(item1, item2));

        // when
        mockMvc.perform(post("/api/v1/orders")
                        .header("X-Huuim-LoginId", user.getLoginId())
                        .header("X-Huuim-LoginPw", user.getLoginPw())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(user.getId()))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.totalPrice").value(
                        (product1.getPrice() * 2) + (product2.getPrice() * 1)
                ));

        /**
         * 🔥 핵심 검증 1: 재고 감소 확인
         */
        Product updatedProduct1 = productRepository.findById(product1.getId()).orElseThrow();
        Product updatedProduct2 = productRepository.findById(product2.getId()).orElseThrow();

        // product1: 10 - 2 = 8
        assertThat(updatedProduct1.getStock()).isEqualTo(8);

        // product2: 5 - 1 = 4
        assertThat(updatedProduct2.getStock()).isEqualTo(4);
    }
}
