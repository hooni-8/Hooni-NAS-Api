package org.nas.api.FilePreviewProvider;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.*;
import org.nas.api.properties.JwtProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class VideoTokenProvider {

    private final SecretKey secretKey;

    public VideoTokenProvider(JwtProperties jwtProperties) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 토큰 검증
     */
    public VideoTokenPayload validate(String token) {

        Claims claims = Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

        return VideoTokenPayload.builder()
                .userCode(claims.get("userCode", String.class))
                .build();
    }
}