package com.dompet.api.features.produtos;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProdutosControllerDeleteTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    record Create(String nome, String descricao, BigDecimal preco, Integer estoque, String imagemUrl, String categoria, Boolean ativo, String sku) {}

    @DynamicPropertySource
    static void randomDb(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:dompet_prod_delete_" + UUID.randomUUID() + ";MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
    }

    private long createOne() throws Exception {
        var dto = new Create("Delete Test", null, new BigDecimal("5.00"), 2, null, "RACAO", true, "SKU-DEL-1");
        var res = mvc.perform(post("/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto))
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
            .andExpect(status().isCreated())
            .andReturn();
        var json = mapper.readTree(res.getResponse().getContentAsString());
        return json.get("id").asLong();
    }

    @Test
    void deveExcluirProdutoRetornando204() throws Exception {
        long id = createOne();
        mvc.perform(delete("/produtos/" + id)
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());
        // Soft delete: buscar novamente deve retornar 200? (depende da listagem). Buscar por ID ainda retorna, pois apenas ativo=false.
        // Aqui validamos que update de ativo ocorreu usando GET search (ativo=true) não deve conter.
        mvc.perform(get("/produtos/" + id))
                .andExpect(status().isOk()) // ainda recuperável por id (ativo flag not filtering this endpoint)
                .andExpect(jsonPath("$.ativo").value(false));
    }

    @Test
    void deveRetornar404AoExcluirProdutoInexistente() throws Exception {
        mvc.perform(delete("/produtos/999999")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Not Found"));
    }
}
