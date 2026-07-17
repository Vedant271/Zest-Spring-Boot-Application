package com.example.ZestSpringBootApplication.repository;

import com.example.ZestSpringBootApplication.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User buildUser(String username, String email, String password) {
        return User.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();
    }

    @Test
    void findByUsername_whenExists_shouldReturnUser() {
        entityManager.persistAndFlush(buildUser("jdoe", "jdoe@example.com", "hashed"));

        Optional<User> result = userRepository.findByUsername("jdoe");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("jdoe@example.com");
    }

    @Test
    void findByUsername_whenNotExists_shouldReturnEmpty() {
        Optional<User> result = userRepository.findByUsername("ghost");

        assertThat(result).isEmpty();
    }

    @Test
    void existsByUsername_whenExists_shouldReturnTrue() {
        entityManager.persistAndFlush(buildUser("asmith", "asmith@example.com", "hashed"));

        assertThat(userRepository.existsByUsername("asmith")).isTrue();
    }

    @Test
    void existsByUsername_whenNotExists_shouldReturnFalse() {
        assertThat(userRepository.existsByUsername("nobody")).isFalse();
    }

    @Test
    void save_withDuplicateUsername_shouldViolateUniqueConstraint() {
        entityManager.persistAndFlush(buildUser("dupe", "dupe1@example.com", "hashed"));
        User duplicateUsername = buildUser("dupe", "dupe2@example.com", "hashed");

        assertThatThrownBy(() -> {
            userRepository.save(duplicateUsername);
            entityManager.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void save_withDuplicateEmail_shouldViolateUniqueConstraint() {
        entityManager.persistAndFlush(buildUser("user1", "shared@example.com", "hashed"));
        User duplicateEmail = buildUser("user2", "shared@example.com", "hashed");

        assertThatThrownBy(() -> {
            userRepository.save(duplicateEmail);
            entityManager.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }
}
