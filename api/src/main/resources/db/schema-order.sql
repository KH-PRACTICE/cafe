-- 주문 테이블
CREATE TABLE orders (
                        order_id BIGINT NOT NULL AUTO_INCREMENT,
                        member_uid BIGINT NOT NULL,
                        status VARCHAR(30) NOT NULL,
                        total_amount BIGINT NOT NULL,
                        ordered_at TIMESTAMP(6) NOT NULL,
                        canceled_at TIMESTAMP(6),
                        updated_at TIMESTAMP(6) NOT NULL,
                        PRIMARY KEY (order_id)
);

CREATE INDEX idx_ord_member_uid ON orders(member_uid);
CREATE INDEX idx_status ON orders(status);
CREATE INDEX idx_ordered_at ON orders(ordered_at);

-- 주문 항목 테이블
CREATE TABLE order_item (
                            order_item_id BIGINT NOT NULL AUTO_INCREMENT,
                            order_id BIGINT NOT NULL,
                            product_id BIGINT NOT NULL,
                            quantity BIGINT NOT NULL,
                            price BIGINT NOT NULL,
                            created_at TIMESTAMP(6) NOT NULL,
                            PRIMARY KEY (order_item_id)
);

CREATE INDEX idx_order_id ON order_item(order_id);
CREATE INDEX idx_product_id ON order_item(product_id);