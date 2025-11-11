package com.store.cafe.member.domain.model.entity;

import java.util.Optional;

public interface MemberIdentityRepository {

    MemberIdentity save(MemberIdentity memberIdentity);

    Optional<MemberIdentity> findByMemberUid(Long memberUid);
}
