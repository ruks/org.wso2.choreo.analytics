package org.wso2.choreo.analytics.gql.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import graphql.ExceptionWhileDataFetching;
import graphql.execution.ExecutionPath;
import graphql.language.SourceLocation;

public class SanitizedError extends ExceptionWhileDataFetching {

    public SanitizedError(ExecutionPath path, Throwable exception, SourceLocation sourceLocation) {
        super(path, exception, sourceLocation);
    }

    @Override
    @JsonIgnore
    public Throwable getException() {
        return super.getException();
    }
}
