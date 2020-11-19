package org.wso2.choreo.analytics.gql.security;

import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;

//@RequiredArgsConstructor
public class BadCredentialsException extends RuntimeException {
    private static final long serialVersionUID = 4129146858129498534L;
    private final String email;

    public BadCredentialsException(String email) {
        this.email = email;
    }

    @Override
    public String getMessage() {
        return MessageFormat.format("Email or password didn''t match for ''{0}''", email);
    }
}
