package org.nas.api.FilePreviewProvider;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.*;
import org.nas.api.properties.JwtProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class VideoTokenProvider {

    private final SecretKey secretKey;

    public VideoTokenProvider(JwtProperties jwtProperties) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 토큰 생성
     */
    public String createToken(
            String userCode,
            String fileId,
            long expireHours) {

        Date now = new Date();

        Date expireDate =
                new Date(
                        now.getTime()
                                + (expireHours * 60 * 60 * 1000)
                );

        return Jwts.builder()
                .claim("userCode", userCode)
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 토큰 검증
     */
    public VideoTokenPayload validate(String token) {

        Claims claims =
                Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

        return VideoTokenPayload.builder()
                .userCode(
                        claims.get(
                                "userCode",
                                String.class
                        )
                )
                .build();
    }
}