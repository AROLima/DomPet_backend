package com.dompet.api.features.usuarios.dto;

import com.dompet.api.shared.endereco.Endereco;
import com.dompet.api.features.usuarios.domain.Role;

public record UsuariosDto(
    String nome,
    String email,
    String telefone,
    Endereco endereco,
    String senha,
    Role role,
    Boolean ativo
) {
    
}
