package com.store.cafe.member;

import com.store.cafe.member.domain.model.entity.MemberPrivate;
import com.store.cafe.member.domain.model.entity.MemberPrivateRepository;
import com.store.cafe.member.domain.model.enums.MemberGenderCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({MemberPrivateRepositoryImpl.class})
class MemberPrivateRepositoryImplTest {

    @Autowired
    private MemberPrivateRepository memberPrivateRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("MemberPrivateRepositoryImpl.save() - 남성 회원의 개인정보를 저장할 수 있다")
    void save_maleGender() {
        // given
        MemberPrivate memberPrivate = MemberPrivate.of(
                100L,
                "encryptedName123",
                "encryptedPhone123",
                MemberGenderCode.MALE,
                "encryptedBirth123"
        );

        // when
        memberPrivateRepository.save(memberPrivate);
        entityManager.flush();

        // then
        MemberPrivate saved = entityManager.find(MemberPrivate.class, 100L);
        assertThat(saved).isNotNull();
        assertThat(saved.getMemberUid()).isEqualTo(100L);
        assertThat(saved.getEncryptedName()).isEqualTo("encryptedName123");
        assertThat(saved.getEncryptedPhone()).isEqualTo("encryptedPhone123");
        assertThat(saved.getGender()).isEqualTo(MemberGenderCode.MALE);
        assertThat(saved.getEncryptedBirth()).isEqualTo("encryptedBirth123");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }
}