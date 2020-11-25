package org.wso2.choreo.analytics.api.security;

public class BadTokenException extends RuntimeException {
    private static final long serialVersionUID = 158136221282852553L;

    public BadTokenException() {
    }

    public BadTokenException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return "Token is invalid or expired";
    }
}
