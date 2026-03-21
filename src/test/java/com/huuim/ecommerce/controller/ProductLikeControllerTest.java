package com.huuim.ecommerce.controller;

import com.huuim.ecommerce.domain.product.Product;
import com.huuim.ecommerce.domain.user.User;
import com.huuim.ecommerce.repository.ProductLikeRepository;
import com.huuim.ecommerce.repository.ProductRepository;
import com.huuim.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 상품 좋아요 API 통합 테스트
 *
 * 테스트 범위:
 * - 좋아요 등록 성공
 * - 중복 좋아요 요청 시 멱등 처리
 * - 좋아요 취소 성공
 * - 중복 취소 요청 시 멱등 처리
 *
 * 설명:
 * 이 테스트는 MockMvc를 사용해서 실제 HTTP 요청처럼 호출하고,
 * 컨트롤러 → 서비스 → 리포지토리 → DB 흐름을 함께 검증한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductLikeControllerTest {
        // 가짜 HTTP 요청을 보내기 위한 MockMvc
        @Autowired
        private MockMvc mockMvc;

        // 테스트 데이터 저장/조회용 Repository
        @Autowired
        private UserRepository userRepository;

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private ProductLikeRepository productLikeRepository;

        // 좋아요 API 테스트용 사용자 생성 헬퍼 메서드
        private User createUser(String loginId, String loginPw, String name) {
                User user = new User(loginId, loginPw, name);
                return userRepository.save(user);
        }

        //좋아요 API 테스트용 상품 생성 헬퍼 메서드
        private Product createProduct(String name, String brand, int price, int stock) {
                Product product = new Product(name, brand, price, stock);
                return productRepository.save(product);
        }

        @Test
        @DisplayName("좋아요 등록 성공")
        void likeProduct_success() throws Exception {
                // given
                User user = createUser("user2", "1234", "강필규");
                Product product = createProduct("로델리아 프리미엄 헤어 케어 필수템 총집합", "Lordelia", 36000, 10);

                // when & then
                mockMvc.perform(post("/api/v1/products/{productId}/likes", product.getId())
                                .header("X-Huuim-LoginId", user.getLoginId())
                                .header("X-Huuim-LoginPw", user.getLoginPw())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.productId").value(product.getId()))
                                .andExpect(jsonPath("$.likeCount").value(1))
                                .andExpect(jsonPath("$.liked").value(true))
                                .andExpect(jsonPath("$.message").value("좋아요가 등록되었습니다."));

                //DB에도 실제 좋아요 이력이 1건 생겼는지 확인
                assertThat(productLikeRepository.existsByUserIdAndProductId(user.getId(), product.getId())).isTrue();

                //상품의 likeCount가 실제로 1 증가했는지 다시 조회해서 확인
                Product foundProduct = productRepository.findById(product.getId()).orElseThrow();
                assertThat(foundProduct.getLikeCount()).isEqualTo(1L);
        }

        @Test
        @DisplayName("같은 사용자가 같은 상품에 다시 좋아요 요청하면 멱등하게 처리된다")
        void likeProduct_idempotent_whenAlreadyLiked() throws Exception {
                // given
                User user = createUser("user2", "1234", "강필규");
                Product product = createProduct("로델리아 프리미엄 헤어 케어 필수템 총집합", "Lordelia", 36000, 10);

                // 첫 번째 좋아요 요청
                mockMvc.perform(post("/api/v1/products/{productId}/likes", product.getId())
                                .header("X-Huuim-LoginId", user.getLoginId())
                                .header("X-Huuim-LoginPw", user.getLoginPw())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isOk());

                // when & then
                // 두 번째 좋아요 요청 이미 좋아요한 상태이므로 likeCount가 더 증가하면 안 된다.
                mockMvc.perform(post("/api/v1/products/{productId}/likes", product.getId())
                                .header("X-Huuim-LoginId", user.getLoginId())
                                .header("X-Huuim-LoginPw", user.getLoginPw())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.productId").value(product.getId()))
                                .andExpect(jsonPath("$.likeCount").value(1))
                                .andExpect(jsonPath("$.liked").value(true))
                                .andExpect(jsonPath("$.message").value("이미 좋아요한 상품입니다."));

                //like 이력이 1건만 존재하는지 확인
                assertThat(productLikeRepository.existsByUserIdAndProductId(user.getId(), product.getId())).isTrue();

                //상품의 likeCount도 여전히 1이어야 한다.
                Product foundProduct = productRepository.findById(product.getId()).orElseThrow();
                assertThat(foundProduct.getLikeCount()).isEqualTo(1L);
        }

        @Test
        @DisplayName("좋아요 취소 성공")
        void unlikeProduct_success() throws Exception {
                // given
                User user = createUser("user2", "1234", "강필규");
                Product product = createProduct("로델리아 프리미엄 헤어 케어 필수템 총집합", "Lordelia", 36000, 10);

                //먼저 좋아요를 1번 등록해 둔다.
                mockMvc.perform(post("/api/v1/products/{productId}/likes", product.getId())
                                .header("X-Huuim-LoginId", user.getLoginId())
                                .header("X-Huuim-LoginPw", user.getLoginPw())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isOk());

                // when & then
                mockMvc.perform(delete("/api/v1/products/{productId}/likes", product.getId())
                                .header("X-Huuim-LoginId", user.getLoginId())
                                .header("X-Huuim-LoginPw", user.getLoginPw()))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.productId").value(product.getId()))
                                .andExpect(jsonPath("$.likeCount").value(0))
                                .andExpect(jsonPath("$.liked").value(false))
                                .andExpect(jsonPath("$.message").value("좋아요가 취소되었습니다."));

                //좋아요 이력이 실제로 삭제되었는지 확인
                assertThat(productLikeRepository.existsByUserIdAndProductId(user.getId(), product.getId())).isFalse();

                //상품의 likeCount가 0으로 감소했는지 확인
                Product foundProduct = productRepository.findById(product.getId()).orElseThrow();
                assertThat(foundProduct.getLikeCount()).isEqualTo(0L);
        }

        @Test
        @DisplayName("이미 좋아요가 없는 상태에서 다시 취소 요청하면 멱등하게 처리된다")
        void unlikeProduct_idempotent_whenAlreadyUnliked() throws Exception {
                // given
                User user = createUser("user2", "1234", "강필규");
                Product product = createProduct("로델리아 프리미엄 헤어 케어 필수템 총집합", "Lordelia", 36000, 10);

                // 좋아요를 등록했다가 한 번 취소해서 현재 상태를 '좋아요 없음' 상태로 변경.
                mockMvc.perform(post("/api/v1/products/{productId}/likes", product.getId())
                                .header("X-Huuim-LoginId", user.getLoginId())
                                .header("X-Huuim-LoginPw", user.getLoginPw())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isOk());

                mockMvc.perform(delete("/api/v1/products/{productId}/likes", product.getId())
                                .header("X-Huuim-LoginId", user.getLoginId())
                                .header("X-Huuim-LoginPw", user.getLoginPw()))
                                .andDo(print())
                                .andExpect(status().isOk());

                // when & then
                // 다시 취소 요청
                mockMvc.perform(delete("/api/v1/products/{productId}/likes", product.getId())
                                .header("X-Huuim-LoginId", user.getLoginId())
                                .header("X-Huuim-LoginPw", user.getLoginPw()))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.productId").value(product.getId()))
                                .andExpect(jsonPath("$.likeCount").value(0))
                                .andExpect(jsonPath("$.liked").value(false))
                                .andExpect(jsonPath("$.message").value("이미 좋아요가 취소된 상태입니다."));

                //상품 likeCount가 음수로 내려가지 않고 0을 유지하는지 확인
                Product foundProduct = productRepository.findById(product.getId()).orElseThrow();
                assertThat(foundProduct.getLikeCount()).isEqualTo(0L);
        }

        @Test
        @DisplayName("존재하지 않는 상품에 좋아요 요청 시 400 반환")
        void likeProduct_fail_whenProductNotFound() throws Exception {
                User user = createUser("user2", "1234", "강필규");
                mockMvc.perform(post("/api/v1/products/{productId}/likes", 99999L)
                                .header("X-Huuim-LoginId", user.getLoginId())
                                .header("X-Huuim-LoginPw", user.getLoginPw()))
                                .andDo(print())
                                .andExpect(status().isBadRequest());
        }
}
