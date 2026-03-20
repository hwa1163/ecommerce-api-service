# Huuim Ecommerce API


휴이엠 과제용 이커머스 API 프로젝트 입니다.  
상품, 좋아요, 주문 서비스를 Spring Boot 기반으로 구현했습니다.

## 1. 기술 스택

- Java
- Spring Boot
- Spring Data JPA
- MySQL
- Maven

1. 회원가입

Method: POST
URI: /api/v1/users

Request
{
  "loginId": "user1",
  "loginPw": "1234",
  "name": "홍길동"
}

Response 예시
{
  "id": 1,
  "loginId": "user1",
  "name": "홍길동",
  "createdAt": "2026-03-20T21:10:00"
};


2. 상품 등록

Method: POST
URI: /api/v1/products

Headers
X-Huuim-LoginId: user1
X-Huuim-LoginPw: 1234
Content-Type: application/json

Request
{
  "name": "에어맥스 97",
  "brand": "NIKE",
  "price": 199000,
  "stock": 10
}
Response 예시
{
  "id": 1,
  "name": "에어맥스 97",
  "brand": "NIKE",
  "price": 199000,
  "stock": 10,
  "likeCount": 0,
  "createdAt": "2026-03-20T21:20:00"
}

3. 상품 목록 조회

Method: GET
URI: /api/v1/products

sort: latest / price_asc / likes_desc
page: 기본값 0
size: 기본값 20

호출 예시
GET /api/v1/products?sort=latest&page=0&size=20


4. 상품 좋아요 등록
Method: POST
URI: /api/v1/products/{productId}/likes

예정

5. 주문 요청
Method: POST
URI: /api/v1/orders

예정



6. 기술 고려사항

과제 요구사항에 따라 아래 항목을 고려하여 구현합니다.
동시성: 좋아요 수, 재고 처리 시 동시 요청 고려
멱등성: 중복 좋아요, 중복 주문 처리 고려
일관성: 주문 처리 시 재고 및 주문 데이터 정합성 유지
느린 조회: 상품 목록 조회 시 정렬/페이징 및 인덱스 고려
동시 주문: 동시에 여러 주문 요청이 들어와도 재고가 정확히 차감되도록 설계

본 과제에서는 ChatGPT, Claude 및 Gemini를 활용했습니다.

활용 내용:
Spring Boot 프로젝트 구조 설계 보조
API 엔드포인트/DTO/서비스 계층 예시 코드 작성 보조