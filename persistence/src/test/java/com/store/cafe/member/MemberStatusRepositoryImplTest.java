package com.store.cafe.member;

import com.store.cafe.member.domain.model.entity.MemberStatus;
import com.store.cafe.member.domain.model.entity.MemberStatusRepository;
import com.store.cafe.member.domain.model.enums.MemberStatusType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({MemberStatusRepositoryImpl.class})
class MemberStatusRepositoryImplTest {

    @Autowired
    private MemberStatusRepository memberStatusRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("MemberStatusRepositoryImpl.saveMemberStatus() - ACTIVE 상태의 회원을 저장할 수 있다")
    void saveMemberStatus_active() {
        // given
        MemberStatus memberStatus = MemberStatus.of(100L, MemberStatusType.ACTIVE);

        // when
        memberStatusRepository.saveMemberStatus(memberStatus);
        entityManager.flush();

        // then
        MemberStatus saved = entityManager.find(MemberStatus.class, 100L);
        assertThat(saved).isNotNull();
        assertThat(saved.getMemberUid()).isEqualTo(100L);
        assertThat(saved.getStatus()).isEqualTo(MemberStatusType.ACTIVE);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.isActive()).isTrue();
    }

    @Test
    @DisplayName("MemberStatusRepositoryImpl.findByMemberUid() - MemberUid 로 회원을 조회할 수 있다")
    void findByMemberUid_found() {
        // given
        MemberStatus memberStatus = MemberStatus.of(300L, MemberStatusType.ACTIVE);
        memberStatusRepository.saveMemberStatus(memberStatus);
        entityManager.flush();
        entityManager.clear();

        // when
        Optional<MemberStatus> found = memberStatusRepository.findByMemberUid(300L);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getMemberUid()).isEqualTo(300L);
        assertThat(found.get().getStatus()).isEqualTo(MemberStatusType.ACTIVE);
        assertThat(found.get().isActive()).isTrue();
    }

    @Test
    @DisplayName("MemberStatusRepositoryImpl.findByMemberUid() - 존재하지 않는 MemberUid로 조회하면 빈 Optional을 반환한다")
    void findByMemberUid_notFound() {
        // when
        Optional<MemberStatus> result = memberStatusRepository.findByMemberUid(99999L);

        // then
        assertThat(result).isEmpty();
    }
}