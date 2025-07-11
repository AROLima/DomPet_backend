package com.dompet.api.models.usuarios;

import com.dompet.api.models.endereco.Endereco;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity


public class Usuarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String senha;
    private String telefone;

    @Embedded
    private Endereco endereco;

    public Usuarios(UsuariosDto data){
        this.nome = data.nome();
        this.email = data.email();
        this.telefone = data.telefone();
        this.endereco = data.endereco();
    }

 }

