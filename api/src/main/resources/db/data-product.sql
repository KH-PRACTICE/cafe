-- 상품
INSERT INTO product (product_id, product_name, price, description, created_at, updated_at)
VALUES (1, '아메리카노', 4500, '깊고 진한 에스프레소에 물을 더한 클래식 커피', NOW(), NOW()),
       (2, '카페라떼', 5000, '부드러운 우유와 에스프레소의 조화', NOW(), NOW()),
       (3, '카푸치노', 5000, '에스프레소와 스팀 우유, 우유 거품이 어우러진 커피', NOW(), NOW()),
       (4, '바닐라라떼', 5500, '달콤한 바닐라 시럽이 들어간 라떼', NOW(), NOW()),
       (5, '카라멜 마키아또', 5500, '카라멜 시럽과 우유 거품이 올라간 달콤한 커피', NOW(), NOW());


-- 상품 재고
INSERT INTO product_stock (product_id, quantity, updated_at)
VALUES (1, 10, NOW()),
       (2, 5, NOW()),
       (3, 1, NOW()),
       (4, 2, NOW()),
       (5, 5, NOW());

