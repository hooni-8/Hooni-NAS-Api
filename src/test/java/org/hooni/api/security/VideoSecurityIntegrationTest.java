package org.hooni.api.security;

import io.jsonwebtoken.Jwts;
import org.hooni.api.model.v1.file.response.FilePreviewResponse;
import org.hooni.api.service.v1.file.FileContentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VideoSecurityIntegrationTest {

    private static final KeyPair KEY_PAIR = generateKeyPair();

    @DynamicPropertySource
    static void jwtProperties(DynamicPropertyRegistry properties) {
        properties.add("jwt.jwk-set-uri", () -> "");
        properties.add("jwt.public-key", () -> Base64.getEncoder().encodeToString(KEY_PAIR.getPublic().getEncoded()));
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FileContentService fileContentService;

    @Test
    void anonymousRangeRequestIsRejected() throws Exception {
        mockMvc.perform(get("/api/v1/file/video/video-001")
                        .param("folderId", "folder-001")
                        .header(HttpHeaders.RANGE, "bytes=2-5"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedRangeRequestReturnsOnlyRequestedBytes() throws Exception {
        byte[] video = "0123456789".getBytes(StandardCharsets.UTF_8);
        when(fileContentService.previewFile("user-code-001", "video-001", "folder-001"))
                .thenReturn(FilePreviewResponse.builder()
                        .resource(new ByteArrayResource(video))
                        .mediaType(MediaType.valueOf("video/mp4"))
                        .build());

        mockMvc.perform(get("/api/v1/file/video/video-001")
                        .param("folderId", "folder-001")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + createAccessToken())
                        .header(HttpHeaders.RANGE, "bytes=2-5"))
                .andExpect(status().isPartialContent())
                .andExpect(header().string(HttpHeaders.CONTENT_RANGE, "bytes 2-5/10"))
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "private, no-store"))
                .andExpect(content().bytes("2345".getBytes(StandardCharsets.UTF_8)));
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
