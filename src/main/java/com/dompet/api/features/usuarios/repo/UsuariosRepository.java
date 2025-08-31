// UsuariosRepository.java
// Repositório JPA para operações com a entidade Usuarios.
// Métodos customizados úteis: existsByEmail e findByEmail retornando Optional.
package com.dompet.api.features.usuarios.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.dompet.api.features.usuarios.domain.Usuarios;
import java.util.Optional;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuarios, Long> {
	boolean existsByEmail(String email);
	Optional<Usuarios> findByEmail(String email);
}
