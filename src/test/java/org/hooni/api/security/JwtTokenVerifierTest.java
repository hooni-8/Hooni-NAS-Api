package org.hooni.api.security;

import io.jsonwebtoken.Jwts;
import org.hooni.api.common.model.DefaultUserInfo;
import org.hooni.api.properties.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenVerifierTest {

    private JwtTokenVerifier verifier;
    private KeyPair keyPair;

    @BeforeEach
    void setUp() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        keyPair = generator.generateKeyPair();

        JwtProperties properties = new JwtProperties();
        properties.setJwkSetUri(null);
        properties.setPublicKey(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
        verifier = new JwtTokenVerifier(properties);
    }

    @Test
    void mapsAccessTokenClaimsToDefaultUserInfo() {
        DefaultUserInfo userInfo = verifier.verifyAccessToken(createToken("accessToken", "hooni-template-api"));

        assertThat(userInfo.getUserCode()).isEqualTo("user-code-001");
        assertThat(userInfo.getUserName()).isEqualTo("Hooni");
        assertThat(userInfo.getRole()).isEqualTo("FREE_USER");
    }

    @Test
    void rejectsRefreshToken() {
        assertThatThrownBy(() -> verifier.verifyAccessToken(createToken("refreshToken", "hooni-template-api")))
                .hasMessageContaining("Access token is required");
    }

    @Test
    void rejectsUnexpectedAudience() {
        assertThatThrownBy(() -> verifier.verifyAccessToken(createToken("accessToken", "another-service")))
                .hasMessageContaining("Unexpected token audience");
    }

    private String createToken(String subject, String audience) {
        Date issuedAt = new Date();
        return Jwts.builder()
                .header().keyId("test-key").and()
                .id(UUID.randomUUID().toString())
                .subject(subject)
                .issuer("hooni-template-auth")
                .claim("aud", audience)
                .claim("userCode", "user-code-001")
                .claim("userName", "Hooni")
                .claim("role", "FREE_USER")
                .issuedAt(issuedAt)
                .expiration(new Date(issuedAt.getTime() + 60_000))
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();
    }
}
