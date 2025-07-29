package ru.mazegen.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class JwtAuthenticationImpl implements Authentication {

    private final JWTUserInfo principal;
    private boolean isAuthenticated;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(principal.getRole().toString()));
    }

    @Override
    public Object getCredentials() {
        return null; // we use custom jwt filter, and token was already checked
    }

    @Override
    public Object getDetails() {
        return null; // additional details not used
    }

    @Override
    public JWTUserInfo getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (principal == null)
            throw new IllegalArgumentException("Can't authenticate when principle is null");
        this.isAuthenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return principal.getNickname();
    }
}
