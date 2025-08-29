package com.dompet.api.features.usuarios.domain;

import com.dompet.api.shared.endereco.Endereco;
import com.dompet.api.features.usuarios.dto.UsuariosDto;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidade de usuários do sistema.
 * Inclui tokenVersion para suportar logout-all (invalidação de tokens antigos).
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

    private String nome;
    private String email;
    private String senha;
    private Boolean ativo = true;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Embedded
    private Endereco endereco;

    // Versão de token: incrementa em logout-all; comparada com claim "ver" no JWT
    @Column(name = "token_version", nullable = false)
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
 }

