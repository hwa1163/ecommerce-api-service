package com.huuim.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huuim.ecommerce.domain.product.Product;
import com.huuim.ecommerce.domain.user.User;
import com.huuim.ecommerce.dto.product.ProductCreateRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 상품 API 테스트
 * - 상품 등록 성공
 * - 인증 헤더 없을 때 실패
 * - 상품 목록 가격 오름차순 정렬 성공
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;


    // 테스트용 사용자 생성
    private User createUser() {
        return userRepository.save(new User("huuim", "huuim1114", "김민회"));
    }

    // 테스트용 상품 생성
    private Product createProduct(String name, int price, int stock) {
        return productRepository.save(new Product(name, "Lordelia", price, stock));
    }

    @Test
    @DisplayName("상품 등록 성공")
    void createProduct_success() throws Exception {
        // given
        User user = createUser();

        ProductCreateRequest request = new ProductCreateRequest();
        request.setName("웰컴 투 휴이엠 샴푸 1000ml");
        request.setBrand("Lordelia");
        request.setPrice(36000);
        request.setStock(10);

        // when & then
        mockMvc.perform(post("/api/v1/products")
                        .header("X-Huuim-LoginId", user.getLoginId())
                        .header("X-Huuim-LoginPw", user.getLoginPw())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("웰컴 투 휴이엠 샴푸 1000ml"))
                .andExpect(jsonPath("$.brand").value("Lordelia"))
                .andExpect(jsonPath("$.price").value(36000))
                .andExpect(jsonPath("$.stock").value(10))
                .andExpect(jsonPath("$.likeCount").value(0));
    }

    @Test
    @DisplayName("상품 등록 시 인증 헤더가 없으면 400 반환")
    void createProduct_fail_whenNoAuthHeader() throws Exception {
        // given
        ProductCreateRequest request = new ProductCreateRequest();
        request.setName("웰컴 투 휴이엠 샴푸 1000ml");
        request.setBrand("Lordelia");
        request.setPrice(36000);
        request.setStock(10);

        // when & then
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("상품 목록 조회 시 가격 오름차순 정렬이 적용")
    void getProducts_success_priceAsc() throws Exception {
        // given
        createProduct("상품A", 30000, 10);
        createProduct("상품B", 10000, 10);
        createProduct("상품C", 20000, 10);

        // when & then
        mockMvc.perform(get("/api/v1/products")
                        .param("sort", "price_asc")
                        .param("page", "0")
                        .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products.length()").value(3))
                //가격 오름차순이면 10000 -> 20000 -> 30000 순서여야 한다.
                .andExpect(jsonPath("$.products[0].price").value(10000))
                .andExpect(jsonPath("$.products[1].price").value(20000))
                .andExpect(jsonPath("$.products[2].price").value(30000));
    }
}