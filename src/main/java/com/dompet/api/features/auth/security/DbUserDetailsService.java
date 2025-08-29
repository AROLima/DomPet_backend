// com/dompet/api/features/auth/security/DbUserDetailsService.java
// Serviço responsável por buscar o usuário no banco a partir do email (username)
// e convertê-lo para um objeto UserDetails que o Spring Security entende.
package com.dompet.api.features.auth.security;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.dompet.api.features.usuarios.repo.UsuariosRepository;

@Service
public class DbUserDetailsService implements UserDetailsService {

  private final UsuariosRepository repo;

  // Injeta o repositório de usuários para consultar o banco
  public DbUserDetailsService(UsuariosRepository repo) { this.repo = repo; }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    // Busca o usuário por email; se não encontrar, lança exceção padrão do Spring Security
    var u = repo.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    // Converte o enum Role para String esperada por UserDetailsBuilder (ex.: USER, ADMIN)
    var role = (u.getRole() == null ? "USER" : u.getRole().name());
    // Monta um User (implementação de UserDetails) com dados do banco
    return User.withUsername(u.getEmail())
        .password(u.getSenha())
        .roles(role) // adiciona prefixo automático "ROLE_" (ex.: gera ROLE_USER)
        // Se ativo for false, desabilita login; null é tratado como ativo (não desabilita)
        .disabled(Boolean.FALSE.equals(u.getAtivo()) ? true : false)
        .build();
  }
}
