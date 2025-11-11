package com.store.cafe.member;

import com.store.cafe.member.domain.model.entity.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberStatusJpaRepository extends JpaRepository<MemberStatus, Long> {
}
