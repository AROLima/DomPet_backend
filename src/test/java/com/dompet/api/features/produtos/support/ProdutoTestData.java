package com.dompet.api.features.produtos.support;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

/** Reusable builder for product test data to reduce duplication. */
public final class ProdutoTestData {
    private static final AtomicInteger SEQ = new AtomicInteger(1);
    private String nome = "Produto";
    private String descricao = "Descricao";
    private BigDecimal preco = new BigDecimal("10.00");
    private Integer estoque = 5;
    private String imagemUrl = null;
    private String categoria = "RACAO";
    private Boolean ativo = true;
    private String sku = "SKU-" + SEQ.getAndIncrement();

    public static ProdutoTestData builder() { return new ProdutoTestData(); }

    public ProdutoTestData nome(String v) { this.nome = v; return this; }
    public ProdutoTestData descricao(String v) { this.descricao = v; return this; }
    public ProdutoTestData preco(BigDecimal v) { this.preco = v; return this; }
    public ProdutoTestData estoque(Integer v) { this.estoque = v; return this; }
    public ProdutoTestData imagemUrl(String v) { this.imagemUrl = v; return this; }
    public ProdutoTestData categoria(String v) { this.categoria = v; return this; }
    public ProdutoTestData ativo(Boolean v) { this.ativo = v; return this; }
    public ProdutoTestData sku(String v) { this.sku = v; return this; }

    public Create build() { return new Create(nome, descricao, preco, estoque, imagemUrl, categoria, ativo, sku); }

    public record Create(String nome, String descricao, BigDecimal preco, Integer estoque, String imagemUrl, String categoria, Boolean ativo, String sku) {}
}
