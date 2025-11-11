package com.store.cafe.member;

import com.store.cafe.member.domain.model.entity.MemberIdentity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberIdentityJpaRepository extends JpaRepository<MemberIdentity, Long> {
}
