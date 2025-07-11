package com.dompet.api.models.usuarios;

import com.dompet.api.models.endereco.Endereco;

public record UsuariosDto(
    String nome,
    String email,
    String telefone,
    Endereco endereco
) {
    
}
