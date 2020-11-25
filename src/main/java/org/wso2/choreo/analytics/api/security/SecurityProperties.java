package org.wso2.choreo.analytics.api.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.time.Duration;

@ConstructorBinding
@ConfigurationProperties(prefix = "whoiswho.security")
@Getter
@RequiredArgsConstructor
public class SecurityProperties {
    /**
     * Amound of hashing iterations, where formula is 2^passwordStrength iterations
     */
    private final int passwordStrength = 10;
    /**
     * Secret used to generate and verify JWT tokens
     */
    private final String tokenSecret = "32a2df3ff6e96fa5ae6fe2512ead23f17d37d6e3c5231bc92f92c9abe2d98a57";
    /**
     * Name of the token issuer
     */
    private final String tokenIssuer = "whoiswho";
    /**
     * Duration after which a token will expire
     */
    private final Duration tokenExpiration = Duration.ofHours(4);

    public int getPasswordStrength() {
        return passwordStrength;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public String getTokenIssuer() {
        return tokenIssuer;
    }

    public Duration getTokenExpiration() {
        return tokenExpiration;
    }
}
