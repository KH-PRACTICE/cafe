package com.store.cafe.member;

import com.store.cafe.member.domain.model.entity.MemberIdentity;
import com.store.cafe.member.domain.model.entity.MemberIdentityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberIdentityRepositoryImpl implements MemberIdentityRepository {

    private final MemberIdentityJpaRepository jpaRepository;

    @Override
    public MemberIdentity save(MemberIdentity memberIdentity) {
        return jpaRepository.save(memberIdentity);
    }

    @Override
    public Optional<MemberIdentity> findByMemberUid(Long memberUid) {
        return jpaRepository.findById(memberUid);
    }
}
