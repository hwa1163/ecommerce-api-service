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