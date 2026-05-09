package com.internship.tool.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;


import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String secret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"; // 256-bit secret

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L); // 1 hour
    }

    @Test
    void testGenerateAndExtractToken() {
        String token = jwtUtil.generateToken("testuser", "ROLE_USER");
        assertNotNull(token);

        String username = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractRole(token);

        assertEquals("testuser", username);
        assertEquals("ROLE_USER", role);
    }

    @Test
    void testValidateToken_Success() {
        String token = jwtUtil.generateToken("testuser", "ROLE_USER");
        assertTrue(jwtUtil.validateToken(token, "testuser"));
    }

    @Test
    void testValidateToken_Failure() {
        String token = jwtUtil.generateToken("testuser", "ROLE_USER");
        assertFalse(jwtUtil.validateToken(token, "wronguser"));
    }
}
