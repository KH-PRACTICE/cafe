-- 상품 테이블
CREATE TABLE product (
                         product_id BIGINT NOT NULL AUTO_INCREMENT,
                         product_name VARCHAR(255) NOT NULL,
                         price BIGINT NOT NULL,
                         description TEXT,
                         created_at TIMESTAMP(6) NOT NULL,
                         updated_at TIMESTAMP(6) NOT NULL,
                         PRIMARY KEY (product_id)
);

CREATE INDEX idx_product_name ON product(product_name);

-- 상품 재고 테이블
CREATE TABLE product_stock (
                               product_id BIGINT NOT NULL,
                               quantity BIGINT NOT NULL,
                               updated_at TIMESTAMP(6) NOT NULL,
                               PRIMARY KEY (product_id)
);