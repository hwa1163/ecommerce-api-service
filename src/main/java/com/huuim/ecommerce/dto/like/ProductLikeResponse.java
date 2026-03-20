package com.huuim.ecommerce.dto.like;

/**
 * 상품 좋아요 응답 DTO
 * - 실제로 이번 요청에서 좋아요가 새로 생성되었는지
 * - 이미 좋아요 상태였는지
 */
public class ProductLikeResponse {

    //좋아요 대상 상품 ID
    private Long productId;

    //현재 상품의 좋아요 수
    private Long likeCount;

    /**
     * 이번 요청으로 새 좋아요가 생성되었는지 여부
     * true  -> 이번 요청으로 새로 좋아요 생성
     * false -> 이미 좋아요 상태였으므로 아무 것도 하지 않음
     */
    private boolean liked;

    //결과 메시지
    private String message;

    public ProductLikeResponse(Long productId, Long likeCount, boolean liked, String message) {
        this.productId = productId;
        this.likeCount = likeCount;
        this.liked = liked;
        this.message = message;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public boolean isLiked() {
        return liked;
    }

    public String getMessage() {
        return message;
    }
}