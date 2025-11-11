package com.store.cafe.member;

import com.store.cafe.member.domain.model.entity.MemberPrivate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberPrivateJpaRepository extends JpaRepository<MemberPrivate, Long> {
}
