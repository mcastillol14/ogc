package com.ogc_prototype.ogc.config;

import com.ogc_prototype.ogc.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    /** Base64 of a 38-byte string — satisfies HMAC-SHA256 minimum of 256 bits */
    private static final String TEST_SECRET =
            Base64.getEncoder().encodeToString("testSecretKeyForTestingPurposesOnly!@#".getBytes());

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtils, "expiration", 86400000L);
    }

    @Test
    void generateToken_returnsNonNullString() {
        String token = jwtUtils.generateToken(1, "alice", Role.CUSTOMER);

        assertThat(token).isNotBlank();
    }

    @Test
    void isTokenValid_withFreshToken_returnsTrue() {
        String token = jwtUtils.generateToken(1, "alice", Role.CUSTOMER);

        assertThat(jwtUtils.isTokenValid(token)).isTrue();
    }

    @Test
    void isTokenValid_withGarbageString_returnsFalse() {
        assertThat(jwtUtils.isTokenValid("not.a.jwt.token")).isFalse();
    }

    @Test
    void isTokenValid_withTamperedSignature_returnsFalse() {
        String token = jwtUtils.generateToken(1, "alice", Role.CUSTOMER);
        String tampered = token.substring(0, token.length() - 4) + "XXXX";

        assertThat(jwtUtils.isTokenValid(tampered)).isFalse();
    }

    @Test
    void extractUserId_returnsCorrectId() {
        String token = jwtUtils.generateToken(42, "bob", Role.ADMIN);

        assertThat(jwtUtils.extractUserId(token)).isEqualTo(42);
    }

    @Test
    void extractUsername_returnsCorrectUsername() {
        String token = jwtUtils.generateToken(1, "charlie", Role.VENDOR);

        assertThat(jwtUtils.extractUsername(token)).isEqualTo("charlie");
    }

    @Test
    void extractRole_returnsCorrectRole() {
        String token = jwtUtils.generateToken(1, "dave", Role.ADMIN);

        assertThat(jwtUtils.extractRole(token)).isEqualTo(Role.ADMIN);
    }

    @Test
    void expiredToken_isNotValid() throws InterruptedException {
        ReflectionTestUtils.setField(jwtUtils, "expiration", 1L); // 1 ms
        String token = jwtUtils.generateToken(1, "expired", Role.CUSTOMER);
        Thread.sleep(10);

        assertThat(jwtUtils.isTokenValid(token)).isFalse();
    }
}
