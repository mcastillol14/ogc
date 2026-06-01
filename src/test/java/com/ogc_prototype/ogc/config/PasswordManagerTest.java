package com.ogc_prototype.ogc.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordManagerTest {

    private PasswordManager passwordManager;

    @BeforeEach
    void setUp() {
        passwordManager = new PasswordManager();
    }

    @Test
    void encode_producesHashDifferentFromRaw() {
        String raw = "mySecretPassword";
        String hash = passwordManager.encode(raw);

        assertThat(hash).isNotEqualTo(raw);
        assertThat(hash).startsWith("$2a$");
    }

    @Test
    void encode_samePasswordProducesDifferentHashes_dueToRandomSalt() {
        String raw = "samePassword";
        String hash1 = passwordManager.encode(raw);
        String hash2 = passwordManager.encode(raw);

        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    void matches_withCorrectPassword_returnsTrue() {
        String raw = "correctPassword";
        String hash = passwordManager.encode(raw);

        assertThat(passwordManager.matches(raw, hash)).isTrue();
    }

    @Test
    void matches_withWrongPassword_returnsFalse() {
        String hash = passwordManager.encode("correctPassword");

        assertThat(passwordManager.matches("wrongPassword", hash)).isFalse();
    }

    @Test
    void matches_withEmptyPassword_returnsFalse() {
        String hash = passwordManager.encode("somePassword");

        assertThat(passwordManager.matches("", hash)).isFalse();
    }
}
