package com.store.cafe.member;

import com.store.cafe.member.domain.model.entity.MemberPassword;
import com.store.cafe.member.domain.model.entity.MemberPasswordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({MemberPasswordRepositoryImpl.class})
class MemberPasswordRepositoryImplTest {

    @Autowired
    private MemberPasswordRepository memberPasswordRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("MemberPasswordRepositoryImpl.save() - 회원 비밀번호를 저장할 수 있다")
    void save_success() {
        // given
        String passwordHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        MemberPassword memberPassword = MemberPassword.of(100L, passwordHash);

        // when
        memberPasswordRepository.save(memberPassword);
        entityManager.flush();

        // then
        MemberPassword saved = entityManager.find(MemberPassword.class, 100L);
        assertThat(saved).isNotNull();
        assertThat(saved.getMemberUid()).isEqualTo(100L);
        assertThat(saved.getPasswordHash()).isEqualTo(passwordHash);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }
}