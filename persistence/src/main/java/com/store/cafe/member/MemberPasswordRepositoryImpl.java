package com.store.cafe.member;

import com.store.cafe.member.domain.model.entity.MemberPassword;
import com.store.cafe.member.domain.model.entity.MemberPasswordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberPasswordRepositoryImpl implements MemberPasswordRepository {

    private final MemberPasswordJpaRepository jpaRepository;

    @Override
    public void save(MemberPassword memberPassword) {
        jpaRepository.save(memberPassword);
    }
}
