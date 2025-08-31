package com.dompet.api.features.usuarios.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;

// DOC: Enum Role — define perfis disponíveis e transforma em GrantedAuthority.
/** Perfis de acesso e suas authorities padrão (ROLE_*) */
public enum Role {
    USER,
    ADMIN;

    public List<GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + name()));
    }
}

