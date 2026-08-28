package org.nas.api.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.nas.api.common.model.DefaultUserInfo;
import org.nas.api.properties.JwtProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtils {

    private static final String ACCESS_TOKEN_SUBJECT = "accessToken";

    private final SecretKey secretKey;

    public JwtUtils(JwtProperties jwtProperties) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Gateway가 전달한 access token의 서명, 만료 시각 및 용도를 검증한다.
     */
    public DefaultUserInfo parseAccessToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            if (!ACCESS_TOKEN_SUBJECT.equals(claims.getSubject())) {
                throw new IllegalArgumentException("Access token is required");
            }

            DefaultUserInfo userInfo = new DefaultUserInfo();
            userInfo.setUserCode(claims.get("userCode", String.class));
            userInfo.setUserName(claims.get("userName", String.class));
            userInfo.setRole(claims.get("role", String.class));
            return userInfo;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid authorization token", e);
        }
    }
}
