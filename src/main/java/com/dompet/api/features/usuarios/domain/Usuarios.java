package com.dompet.api.features.usuarios.domain;

import com.dompet.api.shared.endereco.Endereco;
import com.dompet.api.features.usuarios.dto.UsuariosDto;

import jakarta.persistence.*;
import lombok.*;

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
    
    public void bumpTokenVersion() { this.tokenVersion++; }
    public Integer getTokenVersion() { return tokenVersion; }
    public void setTokenVersion(Integer v) { this.tokenVersion = v; }
 }

