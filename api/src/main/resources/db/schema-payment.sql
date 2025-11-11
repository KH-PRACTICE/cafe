-- 결제 이력 테이블
CREATE TABLE payment_order_history (
                                       payment_id BIGINT NOT NULL AUTO_INCREMENT,
                                       order_id BIGINT NOT NULL,
                                       transaction_id VARCHAR(255),
                                       status VARCHAR(30) NOT NULL,
                                       created_at TIMESTAMP(6),
                                       updated_at TIMESTAMP(6) NOT NULL,
                                       PRIMARY KEY (payment_id),
                                       INDEX idx_payment_order_id (order_id),
                                       INDEX idx_transaction_id (transaction_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;