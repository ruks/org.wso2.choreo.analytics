package org.wso2.choreo.analytics.gql.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Collections;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(JWTFilter.class);

    public JWTUserDetails loadUserByToken(JWTClaimsSet claimsSet) {
        JWTUserDetails details =
                new JWTUserDetails(claimsSet.getSubject(), "carbon.super", "prod", "wso2", Collections.EMPTY_LIST);
        return details;
    }

    @Cacheable("tokenCache")
    public JWTClaimsSet loadTokenFromCache(String token) {
        log.info("cache miss");
        JWTClaimsSet claimsSet;
        try {
            claimsSet = JWTValidator.get().validate(token);
        } catch (ParseException | JOSEException | BadJOSEException e) {
            log.error("Error occurred while validating token", e);
            throw new BadTokenException(e);
        }
        return claimsSet;
    }

    public JWTClaimsSet checkValidity(JWTClaimsSet claimsSet) {
        if (System.currentTimeMillis() > claimsSet.getExpirationTime().getTime()) {
            throw new BadTokenException();
        }
        return claimsSet;
    }

    public JWTUserDetails getCurrentUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .map(user -> (JWTUserDetails) user)
                .orElse(null);
    }

    private boolean isAnonymous(Authentication authentication) {
        return authentication instanceof AnonymousAuthenticationToken;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return null;
    }
}
