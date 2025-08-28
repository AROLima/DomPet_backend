package com.dompet.api.features.usuarios.dto;

import com.dompet.api.shared.endereco.Endereco;

public record UsuariosDto(
    String nome,
    String email,
    String telefone,
    Endereco endereco,
    String senha

) {
    
}
