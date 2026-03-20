# Huuim Ecommerce API


휴이엠 과제용 이커머스 API 프로젝트 입니다.  
상품, 좋아요, 주문 서비스를 Spring Boot 기반으로 구현했습니다.

## 1. 기술 스택

- Java
- Spring Boot
- Spring Data JPA
- MySQL
- Maven

테스트는 테스트용 컨트롤러 및 POSTMAN을 사용하여 진행하였습니다.

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
GET /api/v1/products
GET /api/v1/products?sort=latest&page=0&size=20

상품 목록 조회 - 가격 오름차순
GET api/v1/products?sort=price_asc&page=0&size=20

상품 목록 조회 - 좋아요 많은 순
GET api/v1/products?sort=likes_desc&page=0&size=20


4-1. 상품 좋아요 등록
POST http://localhost:8082/api/v1/products/1/likes
X-Huuim-LoginId: user1
X-Huuim-LoginPw: 1234

body : x

성공시:

{
  "productId": 1,
  "likeCount": 1,
  "liked": true,
  "message": "좋아요가 등록되었습니다."
}

같은요청시:

{
  "productId": 1,
  "likeCount": 1,
  "liked": true,
  "message": "좋아요가 등록되었습니다."
}

4-2. 상품 좋아요 취소

DELETE http://localhost:8082/api/v1/products/1/likes
X-Huuim-LoginId: user1
X-Huuim-LoginPw: 1234

body : x


성공시:

{
  "productId": 1,
  "likeCount": 0,
  "liked": false,
  "message": "좋아요가 취소되었습니다."
}

같은요청시:

{
  "productId": 1,
  "likeCount": 0,
  "liked": false,
  "message": "이미 좋아요가 취소된 상태입니다."
}


- 내가 눌렀던 좋아요만 취소 가능
- 좋아요 취소 시 상품의 likeCount가 1 감소
- 이미 좋아요가 없는 상태에서 다시 취소 요청해도 에러를 내지 않고 멱등하게 처리
- 동시성 이슈 대응을 위해 Product 엔티티에 낙관적 락(`@Version`)을 적용

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