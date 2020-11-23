package org.wso2.choreo.analytics.gql.security;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class JWTUserDetails implements UserDetails {
    private final String username;
    private final String tenant;
    private final String deploymentId;
    private final String customerId;
    private final List<SimpleGrantedAuthority> authorities;

    public JWTUserDetails(String username, String tenant, String deploymentId, String customerId,
            List<SimpleGrantedAuthority> authorities) {
        this.username = username;
        this.tenant = tenant;
        this.deploymentId = deploymentId;
        this.customerId = customerId;
        this.authorities = authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public List<SimpleGrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return UUID.randomUUID().toString();
    }

    public String getTenant() {
        return tenant;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public String getCustomerId() {
        return customerId;
    }

}
