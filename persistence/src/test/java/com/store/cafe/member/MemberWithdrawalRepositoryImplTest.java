package com.store.cafe.member;

import com.store.cafe.member.domain.model.entity.MemberWithdrawalRepository;
import com.store.cafe.member.domain.model.entity.MemberWithdrawalSummary;
import com.store.cafe.member.domain.model.enums.WithdrawalStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({MemberWithdrawalRepositoryImpl.class})
class MemberWithdrawalRepositoryImplTest {

    @Autowired
    private MemberWithdrawalRepository memberWithdrawalRepository;

    @Test
    @DisplayName("MemberWithdrawalRepositoryImpl.save() - 회원 탈퇴 summay 데이터를 저장할 수 있다")
    void save_success() {
        // given
        MemberWithdrawalSummary withdrawal = MemberWithdrawalSummary.of(
                100L, 
                "loginIdHash123", 
                WithdrawalStatus.REQUESTED, 
                "재가입 예정",
                ZonedDateTime.now()
        );

        // when
        MemberWithdrawalSummary saved = memberWithdrawalRepository.save(withdrawal);

        // then
        assertThat(saved).isNotNull();
        assertThat(saved.getMemberUid()).isEqualTo(100L);
        assertThat(saved.getReason()).isEqualTo("재가입 예정");
        assertThat(saved.getStatus()).isEqualTo(WithdrawalStatus.REQUESTED);
        assertThat(saved.getRequestedAt()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("MemberWithdrawalRepositoryImpl.findByMemberUid() -  MemberUid 로 회원을 조회할 수 있다")
    void findByMemberUid_found() {
        // given
        MemberWithdrawalSummary withdrawal = MemberWithdrawalSummary.of(
                200L, 
                "loginIdHash200", 
                WithdrawalStatus.REQUESTED, 
                "재가입 예정",
                ZonedDateTime.now()
        );
        memberWithdrawalRepository.save(withdrawal);

        // when
        Optional<MemberWithdrawalSummary> found = memberWithdrawalRepository.findByMemberUid(200L);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getMemberUid()).isEqualTo(200L);
        assertThat(found.get().getReason()).isEqualTo("재가입 예정");
        assertThat(found.get().getStatus()).isEqualTo(WithdrawalStatus.REQUESTED);
    }

    @Test
    @DisplayName("MemberWithdrawalRepositoryImpl.findByMemberUid() - 존재하지 않는 MemberUid로 조회하면 빈 Optional을 반환한다")
    void findByMemberUid_notFound() {
        // when
        Optional<MemberWithdrawalSummary> result = memberWithdrawalRepository.findByMemberUid(99999L);

        // then
        assertThat(result).isEmpty();
    }
}