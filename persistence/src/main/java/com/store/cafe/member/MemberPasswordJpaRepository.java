package com.store.cafe.member;

import com.store.cafe.member.domain.model.entity.MemberPassword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberPasswordJpaRepository extends JpaRepository<MemberPassword, Long> {
}
