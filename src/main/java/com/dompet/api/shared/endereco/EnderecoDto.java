package com.dompet.api.shared.endereco;

/** DTO de endereço com conversões para/desde a entidade embutida. */
public record EnderecoDto(
    String rua,
    String numero,
    String bairro,
    String cep,
    String cidade,
    String complemento
) {
    /** Cria um DTO a partir da entidade (null-safe). */
    public static EnderecoDto from(Endereco e) {
        if (e == null) return null;
        return new EnderecoDto(e.getRua(), e.getNumero(), e.getBairro(), e.getCep(), e.getCidade(), e.getComplemento());
    }
    /** Converte o DTO em entidade embutida. */
    public Endereco toEntity() {
        var e = new Endereco();
        e.setRua(rua);
        e.setNumero(numero);
        e.setBairro(bairro);
        e.setCep(cep);
        e.setCidade(cidade);
        e.setComplemento(complemento);
        return e;
    }
}
