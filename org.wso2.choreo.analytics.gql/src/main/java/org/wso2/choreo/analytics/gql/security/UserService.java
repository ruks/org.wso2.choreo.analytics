package org.wso2.choreo.analytics.gql.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(JWTFilter.class);

    @Transactional
    public JWTUserDetails loadUserByToken(String token) {
        AuthenticationContext context = null;
        try {
            context = JWTValidator.get().validate(token);
            JWTUserDetails details = new JWTUserDetails(context.getUsername(), "foo.com", "prod", "Laposte",
                    Collections.EMPTY_LIST);
            return details;
        } catch (Exception e) {
            log.error("Error occurred while validating token", e);
            throw new BadTokenException(e);
        }
    }

    @Transactional
    public JWTUserDetails getCurrentUser() {
        return Optional
            .ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .map(Authentication::getPrincipal)
            .map(user -> (JWTUserDetails) user)
            .orElse(null);
    }

    public boolean isAdmin() {
        return true;
//        return Optional
//            .ofNullable(SecurityContextHolder.getContext())
//            .map(SecurityContext::getAuthentication)
//            .map(Authentication::getAuthorities)
//            .stream()
//            .flatMap(Collection::stream)
//            .map(GrantedAuthority::getAuthority)
//            .anyMatch(ADMIN_AUTHORITY::equals);
    }

    public boolean isAuthenticated() {
        return true;
//        return Optional
//            .ofNullable(SecurityContextHolder.getContext())
//            .map(SecurityContext::getAuthentication)
//            .filter(Authentication::isAuthenticated)
//            .filter(not(this::isAnonymous))
//            .isPresent();
    }

    private boolean isAnonymous(Authentication authentication) {
        return authentication instanceof AnonymousAuthenticationToken;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return null;
    }
}
