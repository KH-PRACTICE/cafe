package com.store.cafe.member;

import com.store.cafe.member.domain.model.entity.MemberPrivate;
import com.store.cafe.member.domain.model.entity.MemberPrivateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberPrivateRepositoryImpl implements MemberPrivateRepository {

    private final MemberPrivateJpaRepository jpaRepository;

    @Override
    public void save(MemberPrivate memberPrivate) {
        jpaRepository.save(memberPrivate);
    }
}
