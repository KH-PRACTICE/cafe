-- 회원 식별 정보 테이블
CREATE TABLE member_identity (
                                 member_uid BIGINT NOT NULL AUTO_INCREMENT,
                                 login_id VARCHAR(50) NOT NULL UNIQUE,
                                 created_at TIMESTAMP(6) NOT NULL,
                                 update_dt TIMESTAMP(6) NOT NULL,
                                 PRIMARY KEY (member_uid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 회원 비밀번호 테이블
CREATE TABLE member_password (
                                 member_uid BIGINT NOT NULL,
                                 password_hash VARCHAR(255) NOT NULL,
                                 created_at TIMESTAMP(6) NOT NULL,
                                 update_dt TIMESTAMP(6) NOT NULL,
                                 PRIMARY KEY (member_uid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 회원 개인정보 테이블
CREATE TABLE member_private (
                                member_uid BIGINT NOT NULL,
                                encrypted_name VARCHAR(255) NOT NULL,
                                encrypted_phone VARCHAR(255) NOT NULL,
                                gender VARCHAR(10) NOT NULL,
                                encrypted_birth VARCHAR(255) NOT NULL,
                                created_at TIMESTAMP(6) NOT NULL,
                                update_dt TIMESTAMP(6) NOT NULL,
                                PRIMARY KEY (member_uid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 회원 상태 테이블
CREATE TABLE member_status (
                               member_uid BIGINT NOT NULL,
                               status VARCHAR(30) NOT NULL,
                               created_at TIMESTAMP(6) NOT NULL,
                               updated_at TIMESTAMP(6) NOT NULL,
                               PRIMARY KEY (member_uid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 회원 탈퇴 감사 로그 테이블
CREATE TABLE member_withdrawal_audit_log (
                                             id BIGINT NOT NULL AUTO_INCREMENT,
                                             member_uid BIGINT NOT NULL,
                                             login_id_hash VARCHAR(255) NOT NULL,
                                             event_type VARCHAR(30) NOT NULL,
                                             created_at TIMESTAMP(6) NOT NULL,
                                             PRIMARY KEY (id),
                                             INDEX idx_member_uid (member_uid),
                                             INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 회원 탈퇴 요약 테이블
CREATE TABLE member_withdrawal_summary (
                                           member_uid BIGINT NOT NULL,
                                           login_id_hash VARCHAR(255) NOT NULL,
                                           status VARCHAR(30) NOT NULL,
                                           requested_at TIMESTAMP(6),
                                           canceled_at TIMESTAMP(6),
                                           confirmed_at TIMESTAMP(6),
                                           scheduled_delete_at TIMESTAMP(6),
                                           reason VARCHAR(500),
                                           created_at TIMESTAMP(6) NOT NULL,
                                           updated_at TIMESTAMP(6) NOT NULL,
                                           PRIMARY KEY (member_uid),
                                           INDEX idx_withdrawal_summary_status (status),
                                           INDEX idx_withdrawal_summary_scheduled_delete_at (scheduled_delete_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
