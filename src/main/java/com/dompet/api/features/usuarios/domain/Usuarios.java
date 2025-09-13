package com.dompet.api.features.usuarios.domain;

import com.dompet.api.shared.endereco.Endereco;
import com.dompet.api.features.usuarios.dto.UsuariosDto;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Entidade de usuários do sistema.
 *
 * Bloco metodológico:
 * - Representa identidade e dados básicos do usuário (nome, email, senha, role).
 * - Mantém `tokenVersion` para implementar logout-all: quando incrementada, tokens
 *   previamente emitidos (com claim "ver" antigo) deixam de ser válidos.
 * - Usa `@Embedded Endereco` para compor informações de endereço sem tabela separada.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "nome obrigatorio")
    @Size(max = 255, message = "nome max 255")
    private String nome;

    @NotBlank(message = "email obrigatorio")
    @Email(message = "email invalido")
    @Size(max = 255, message = "email max 255")
    private String email;

    @NotBlank(message = "senha obrigatoria")
    private String senha;

    @NotNull(message = "ativo obrigatorio")
    private Boolean ativo = true;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "role obrigatoria")
    private Role role;

    @Embedded
    private Endereco endereco;

    // Versão de token: incrementa em logout-all; comparada com claim "ver" no JWT
    @Column(name = "token_version", nullable = false)
    @NotNull(message = "tokenVersion obrigatorio")
    @PositiveOrZero(message = "tokenVersion >= 0")
    private Integer tokenVersion = 0;

    public Usuarios(UsuariosDto data){
        this.nome = data.nome();
        this.email = data.email();
        this.senha = data.senha();
        this.endereco = data.endereco();
        this.role = data.role();
        this.ativo = data.ativo();
    }
    
    /** Incrementa a versão do token (logout-all). */
    public void bumpTokenVersion() { this.tokenVersion++; }
    public Integer getTokenVersion() { return tokenVersion; }
    public void setTokenVersion(Integer v) { this.tokenVersion = v; }

    // Nota: armazenar tokenVersion no usuario evita necessidade de tabela de blacklist e
    // traz uma implementação simples para invalidar tokens antigos.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuarios that = (Usuarios) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
 }

