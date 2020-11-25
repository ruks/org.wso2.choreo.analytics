package org.wso2.choreo.analytics.api.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class JWTFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final Pattern BEARER_PATTERN = Pattern.compile("^Bearer (.+?)$");
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(JWTFilter.class);

    public JWTFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException,
            ServletException {
        try {
            getToken(request)
                .map(userService::loadTokenFromCache)
                .map(userService::checkValidity)
                .map(userService::loadUserByToken)
                .map(userDetails -> JWTPreAuthenticationToken
                    .builder()
                    .principal(userDetails)
                    .details(new WebAuthenticationDetailsSource().buildDetails(request))
                    .build())
                .ifPresent(authentication -> SecurityContextHolder.getContext().setAuthentication(
                        (Authentication) authentication));
        } catch (BadTokenException e) {
            log.error("Error occurred while validating token.", e);
        }
        filterChain.doFilter(request, response);

    }

    private Optional<String> getHeaderToken(HttpServletRequest request) {
        return Optional
            .ofNullable(request.getHeader(AUTHORIZATION_HEADER))
            .filter(Predicate.not(String::isEmpty))
            .map(BEARER_PATTERN::matcher)
            .filter(Matcher::find)
            .map(matcher -> matcher.group(1));
    }

    private Optional<String> getToken(HttpServletRequest request) {
        Cookie[] cookieVal = request.getCookies();
        String part1Name = "capp1";
        String part2Name = "capp2";
        String part1Value = null;
        String part2Value = null;
        if (cookieVal != null) {
            for (Cookie aCookie : cookieVal) {
                if (part1Name.equalsIgnoreCase(aCookie.getName())) {
                    part1Value = aCookie.getValue();
                }
                if (part2Name.equalsIgnoreCase(aCookie.getName())) {
                    part2Value = aCookie.getValue();
                }
            }
        }

        if (part1Value != null && part2Value != null) {
            return Optional.of(part1Value + part2Value);

        }

        return getHeaderToken(request);

    }
}
