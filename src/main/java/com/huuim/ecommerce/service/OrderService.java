package com.huuim.ecommerce.service;

import com.huuim.ecommerce.domain.order.Order;
import com.huuim.ecommerce.domain.order.OrderItem;
import com.huuim.ecommerce.domain.product.Product;
import com.huuim.ecommerce.domain.user.User;
import com.huuim.ecommerce.dto.order.OrderCreateItemRequest;
import com.huuim.ecommerce.dto.order.OrderCreateRequest;
import com.huuim.ecommerce.dto.order.OrderResponse;
import com.huuim.ecommerce.repository.OrderRepository;
import com.huuim.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 주문 서비스
 * - 주문 요청 검증
 * - 여러 상품 주문 처리
 * - 재고 차감
 * - 주문/주문상세 저장
 * - 동시 주문 시 재고 정합성 확보
 */
@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    /**
     * 주문 생성
     * 1. 요청값 검증
     * 2. 주문 생성
     * 3. 각 상품을 비관적 락으로 조회
     * 4. 재고 검증 및 차감
     * 5. OrderItem 생성 및 주문에 추가
     * 6. 주문 저장
     */
    @Transactional
    public OrderResponse createOrder(User user, OrderCreateRequest request) {
        validateOrderRequest(request);

        Order order = new Order(user);

        //같은 상품이 주문 항목에 중복으로 들어오면 계산/재고 차감이 꼬일 수 있으므로 방어한다.
        validateDuplicateProductIds(request.getItems());

        for (OrderCreateItemRequest itemRequest : request.getItems()) {
            // 재고 차감 정합성을 위해 비관적 락 조회
            Product product = productRepository.findByIdForUpdate(itemRequest.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "해당 상품을 찾을 수 없습니다. productId = " + itemRequest.getProductId()
                    ));

            //상품 엔티티 내부에서 재고 부족 여부 검증 후 차감
            product.decreaseStock(itemRequest.getQuantity());

            //주문 시점 가격을 별도로 저장
            OrderItem orderItem = new OrderItem(
                    product,
                    itemRequest.getQuantity(),
                    product.getPrice()
            );

            order.addOrderItem(orderItem);
        }

        Order savedOrder = orderRepository.save(order);
        return OrderResponse.from(savedOrder);
    }

    //주문 요청 기본 검증
    private void validateOrderRequest(OrderCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("주문 요청은 필수입니다.");
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("주문 항목은 최소 1개 이상이어야 합니다.");
        }

        for (OrderCreateItemRequest item : request.getItems()) {
            if (item.getProductId() == null) {
                throw new IllegalArgumentException("productId는 필수입니다.");
            }

            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("quantity는 1 이상이어야 합니다.");
            }
        }
    }

    /**
     * 같은 상품이 한 주문 요청 안에 중복 포함되는지 검증
     * ex)- productId=1, quantity=2
     * ex)- productId=1, quantity=3
     *
     * 이런 입력은 재고 처리도 헷갈리므로 막음
     */
    private void validateDuplicateProductIds(List<OrderCreateItemRequest> items) {
        Set<Long> productIds = new HashSet<>();

        for (OrderCreateItemRequest item : items) {
            if (!productIds.add(item.getProductId())) {
                throw new IllegalArgumentException("하나의 주문에 동일한 상품을 중복해서 담을 수 없습니다. productId = " + item.getProductId());
            }
        }
    }
}