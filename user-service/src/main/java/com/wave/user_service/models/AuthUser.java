package com.wave.user_service.models;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.jspecify.annotations.Nullable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "auth_users")
public class AuthUser implements UserDetails {
    private UUID id;
    private String username;
    private String passwordHash;
    private UserStatus status;
    private List<UserRole> roles;
    private Instant createdAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
            .map(role -> "ROLE_" + role.name())
            .map(SimpleGrantedAuthority::new)
            .toList();
    }

    @Override
    public @Nullable String getPassword() {
        return passwordHash;
    }

    public enum UserRole {
        HR,
        MANAGER,
        ADMIN
    }

    public enum UserStatus {
        ACTIVE,
        INACTIVE,
        SUSPENDED
    }
}
