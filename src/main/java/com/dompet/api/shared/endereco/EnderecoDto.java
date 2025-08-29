package com.dompet.api.shared.endereco;

public record EnderecoDto(
    String rua,
    String numero,
    String bairro,
    String cep,
    String cidade,
    String complemento
) {
    public static EnderecoDto from(Endereco e) {
        if (e == null) return null;
        return new EnderecoDto(e.getRua(), e.getNumero(), e.getBairro(), e.getCep(), e.getCidade(), e.getComplemento());
    }
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
