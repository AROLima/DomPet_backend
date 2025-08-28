// com/dompet/api/features/auth/security/DbUserDetailsService.java
package com.dompet.api.features.auth.security;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.dompet.api.features.usuarios.repo.UsuariosRepository;

@Service
public class DbUserDetailsService implements UserDetailsService {

  private final UsuariosRepository repo;

  public DbUserDetailsService(UsuariosRepository repo) { this.repo = repo; }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    var u = repo.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
  var role = (u.getRole() == null ? "USER" : u.getRole().name());
    return User.withUsername(u.getEmail())
        .password(u.getSenha())
        .roles(role) // gera "ROLE_USER", "ROLE_ADMIN"
    .disabled(Boolean.FALSE.equals(u.getAtivo()) ? true : false)
        .build();
  }
}
