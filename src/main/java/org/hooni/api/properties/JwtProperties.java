package org.hooni.api.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.AssertTrue;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    @NotBlank
    @Pattern(regexp = "RS256")
    private String algorithm = "RS256";

    @NotBlank
    private String issuer = "hooni-template-auth";

    @NotBlank
    private String audience = "hooni-template-api";

    private String jwkSetUri;
    private String publicKey;

    @AssertTrue(message = "exactly one of jwk-set-uri or public-key must be configured")
    public boolean isVerificationSourceConfigured() {
        return isBlank(jwkSetUri) != isBlank(publicKey);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
