package org.wso2.choreo.analytics.api.security;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.wso2.choreo.analytics.api.gql.Environment;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

public class MySecurityExpressionRoot implements MethodSecurityExpressionOperations {
    protected final Authentication authentication;
    protected final Environment environment;
    private AuthenticationTrustResolver trustResolver;
    private RoleHierarchy roleHierarchy;
    private Set<String> roles;
    private String defaultRolePrefix = "ROLE_";
    public final boolean permitAll = true;
    public final boolean denyAll = false;
    private PermissionEvaluator permissionEvaluator;
    public final String read = "read";
    public final String write = "write";
    public final String create = "create";
    public final String delete = "delete";
    public final String admin = "administration";

    public MySecurityExpressionRoot(Authentication authentication, Environment environment) {
        if (authentication == null) {
            throw new IllegalArgumentException("Authentication object cannot be null");
        }
        this.authentication = authentication;
        this.environment = environment;
    }

    public final boolean hasAuthority(String authority) {
        return this.hasAnyAuthority(authority);
    }

    public final boolean hasAnyAuthority(String... authorities) {
        return this.hasAnyAuthorityName((String)null, authorities);
    }

    public final boolean hasRole(String role) {
        return this.hasAnyRole(role);
    }

    public final boolean hasAnyRole(String... roles) {
        return this.hasAnyAuthorityName(this.defaultRolePrefix, roles);
    }

    private boolean hasAnyAuthorityName(String prefix, String... roles) {
        Set<String> roleSet = this.getAuthoritySet();
        String[] var4 = roles;
        int var5 = roles.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            String role = var4[var6];
            String defaultedRole = getRoleWithDefaultPrefix(prefix, role);
            if (roleSet.contains(defaultedRole)) {
                return true;
            }
        }
        return false;
    }

    public final Authentication getAuthentication() {
        return this.authentication;
    }

    public final boolean permitAll() {
        return true;
    }

    public final boolean denyAll() {
        return false;
    }

    public final boolean isAnonymous() {
        return this.trustResolver.isAnonymous(this.authentication);
    }

    public final boolean isAuthenticated() {
        return !this.isAnonymous() && hasEnvironment();
    }

    public final boolean isRememberMe() {
        return this.trustResolver.isRememberMe(this.authentication);
    }

    public final boolean isFullyAuthenticated() {
        return !this.trustResolver.isAnonymous(this.authentication) && !this.trustResolver.isRememberMe(this.authentication);
    }

    @Override
    public void setFilterObject(Object o) {

    }

    @Override
    public Object getFilterObject() {
        return null;
    }

    @Override
    public void setReturnObject(Object o) {

    }

    @Override
    public Object getReturnObject() {
        return null;
    }

    @Override
    public Object getThis() {
        return null;
    }

    public void setTrustResolver(AuthenticationTrustResolver trustResolver) {
        this.trustResolver = trustResolver;
    }

    public void setRoleHierarchy(RoleHierarchy roleHierarchy) {
        this.roleHierarchy = roleHierarchy;
    }

    public void setDefaultRolePrefix(String defaultRolePrefix) {
        this.defaultRolePrefix = defaultRolePrefix;
    }

    private Set<String> getAuthoritySet() {
        if (this.roles == null) {
            Collection<? extends GrantedAuthority> userAuthorities = this.authentication.getAuthorities();
            if (this.roleHierarchy != null) {
                userAuthorities = this.roleHierarchy.getReachableGrantedAuthorities(userAuthorities);
            }

            this.roles = AuthorityUtils.authorityListToSet(userAuthorities);
        }

        return this.roles;
    }

    public boolean hasPermission(Object target, Object permission) {
        return this.permissionEvaluator.hasPermission(this.authentication, target, permission);
    }

    public boolean hasPermission(Object targetId, String targetType, Object permission) {
        return this.permissionEvaluator.hasPermission(this.authentication, (Serializable) targetId, targetType, permission);
    }

    public void setPermissionEvaluator(PermissionEvaluator permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
    }

    private static String getRoleWithDefaultPrefix(String defaultRolePrefix, String role) {
        if (role == null) {
            return role;
        } else if (defaultRolePrefix != null && defaultRolePrefix.length() != 0) {
            return role.startsWith(defaultRolePrefix) ? role : defaultRolePrefix + role;
        } else {
            return role;
        }
    }

    private boolean hasEnvironment() {
        JWTUserDetails userDetails = (JWTUserDetails) this.authentication.getPrincipal();
        if(this.environment == null || StringUtils.isBlank(this.environment.getName()) || StringUtils.isBlank(userDetails.getDeploymentId())) {
            return false;
        }
        return this.environment.getName().equals(userDetails.getDeploymentId());
    }
}
