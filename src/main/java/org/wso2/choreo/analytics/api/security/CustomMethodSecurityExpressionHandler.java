package org.wso2.choreo.analytics.api.security;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.wso2.choreo.analytics.api.gql.Environment;

public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {
    private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication,
            MethodInvocation invocation) {
        Environment environment = null;
        Object[] objects = invocation.getArguments();
        if(objects != null) {
            for (Object o : objects) {
                if(o instanceof Environment) {
                    environment = (Environment) o;
                    break;
                }
            }
        }

        CustomSecurityExpressionRoot root = new CustomSecurityExpressionRoot(authentication, environment);
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(this.trustResolver);
        root.setRoleHierarchy(getRoleHierarchy());
        return root;
    }
}
