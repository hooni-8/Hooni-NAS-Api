package org.nas.api.FilePreviewProvider;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.*;
import org.nas.api.properties.JwtProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class VideoTokenProvider {

    private static final String VIDEO_SUBJECT = "videoToken";

    private final SecretKey secretKey;

    public VideoTokenProvider(JwtProperties jwtProperties) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 토큰 검증
     */
    public VideoTokenPayload validate(String token, String requestedFileId) {

        Claims claims = Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

        String userCode = claims.get("userCode", String.class);
        String fileId = claims.get("fileId", String.class);

        if (!VIDEO_SUBJECT.equals(claims.getSubject())
                || userCode == null || userCode.isBlank()
                || fileId == null || fileId.isBlank()
                || !fileId.equals(requestedFileId)) {
            throw new IllegalArgumentException("Invalid video token");
        }

        return VideoTokenPayload.builder()
                .userCode(userCode)
                .fileId(fileId)
                .build();
    }
}
