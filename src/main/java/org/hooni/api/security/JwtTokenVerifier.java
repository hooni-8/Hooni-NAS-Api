package org.hooni.api.security;

import org.hooni.api.common.model.DefaultUserInfo;
import org.hooni.api.properties.JwtProperties;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/** Auth API의 JWKS 공개키로 Access Token을 검증한다. */
@Component
public class JwtTokenVerifier {

    private static final String ACCESS_TOKEN_SUBJECT = "accessToken";

    private final JwtProperties properties;
    private final JwtDecoder decoder;

    @Autowired
    public JwtTokenVerifier(JwtProperties properties) {
        this(properties, createDecoder(properties));
    }

    JwtTokenVerifier(JwtProperties properties, JwtDecoder decoder) {
        this.properties = properties;
        this.decoder = decoder;
    }

    public DefaultUserInfo verifyAccessToken(String token) {
        Jwt jwt = decoder.decode(token);
        if (!ACCESS_TOKEN_SUBJECT.equals(jwt.getSubject())) {
            throw new BadJwtException("Access token is required");
        }
        if (!jwt.getAudience().contains(properties.getAudience())) {
            throw new BadJwtException("Unexpected token audience");
        }
        if (jwt.getId() == null || jwt.getId().isBlank()) {
            throw new BadJwtException("Token id is required");
        }
        if (jwt.getExpiresAt() == null) {
            throw new BadJwtException("Token expiration is required");
        }

        String role = requireTextClaim(jwt, "role");
        if (!role.matches("[A-Z][A-Z0-9_]{0,63}")) {
            throw new BadJwtException("Invalid role claim");
        }

        return DefaultUserInfo.builder()
                .userCode(requireTextClaim(jwt, "userCode"))
                .userName(jwt.getClaimAsString("userName"))
                .role(role)
                .build();
    }

    private String requireTextClaim(Jwt jwt, String name) {
        String value = jwt.getClaimAsString(name);
        if (value == null || value.isBlank()) {
            throw new BadJwtException("Token claim is required: " + name);
        }
        return value;
    }

    private static JwtDecoder createDecoder(JwtProperties properties) {
        NimbusJwtDecoder decoder;
        if (properties.getPublicKey() != null && !properties.getPublicKey().isBlank()) {
            decoder = NimbusJwtDecoder.withPublicKey(parsePublicKey(properties.getPublicKey()))
                    .signatureAlgorithm(SignatureAlgorithm.RS256)
                    .build();
        } else {
            decoder = NimbusJwtDecoder.withJwkSetUri(properties.getJwkSetUri())
                    .jwsAlgorithm(SignatureAlgorithm.RS256)
                    .build();
        }
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(properties.getIssuer()));
        return decoder;
    }

    private static RSAPublicKey parsePublicKey(String value) {
        try {
            byte[] bytes = Base64.getDecoder().decode(value.replaceAll("\\s", ""));
            return (RSAPublicKey) KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(bytes));
        } catch (Exception exception) {
            throw new IllegalArgumentException("jwt.public-key must be a Base64 X.509 RSA public key", exception);
        }
    }
}
