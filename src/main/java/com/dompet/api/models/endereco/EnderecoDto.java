package com.dompet.api.models.endereco;

public record EnderecoDto(
    String rua,
    String numero,
    String bairro,
    String cep,
    String cidade,
    String complemento

) {
    
}
