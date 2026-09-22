package org.hooni.api.security;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    private static final KeyPair KEY_PAIR = generateKeyPair();

    @DynamicPropertySource
    static void jwtProperties(DynamicPropertyRegistry properties) {
        properties.add("jwt.jwk-set-uri", () -> "");
        properties.add("jwt.public-key", () -> Base64.getEncoder().encodeToString(KEY_PAIR.getPublic().getEncoded()));
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthEndpointIsPublic() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("UP"));
    }

    @Test
    void protectedEndpointRejectsAnonymousRequest() throws Exception {
        mockMvc.perform(get("/api/v1/example/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("0003"));
    }

    @Test
    void authenticationPrincipalContainsUserCodeClaim() throws Exception {
        mockMvc.perform(get("/api/v1/example/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + createAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userCode").value("user-code-001"))
                .andExpect(jsonPath("$.data.userName").value("Hooni"))
                .andExpect(jsonPath("$.data.role").value("FREE_USER"));
    }

    @Test
    void myBatisExampleReadsDatabaseTime() throws Exception {
        mockMvc.perform(get("/api/v1/example/database-time")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + createAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.databaseTime").exists());
    }

    private String createAccessToken() {
        Date issuedAt = new Date();
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject("accessToken")
                .issuer("hooni-template-auth")
                .claim("aud", "hooni-template-api")
                .claim("userCode", "user-code-001")
                .claim("userName", "Hooni")
                .claim("role", "FREE_USER")
                .issuedAt(issuedAt)
                .expiration(new Date(issuedAt.getTime() + 60_000))
                .signWith(KEY_PAIR.getPrivate(), Jwts.SIG.RS256)
                .compact();
    }

    private static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }
}
