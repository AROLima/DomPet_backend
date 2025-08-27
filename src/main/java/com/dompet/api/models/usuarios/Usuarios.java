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
    private Boolean ativo = true;




    @Embedded
    private Endereco endereco;

    public Usuarios(UsuariosDto data){
        this.nome = data.nome();
        this.email = data.email();
        this.senha = data.senha();
        this.endereco = data.endereco();
    }

 }

