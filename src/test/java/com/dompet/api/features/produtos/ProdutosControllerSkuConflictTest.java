package com.dompet.api.features.produtos;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testa conflito de SKU (unique constraint) retornando ProblemDetail 409.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProdutosControllerSkuConflictTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    record Create(String nome, String descricao, BigDecimal preco, Integer estoque, String imagemUrl, Boolean ativo, String categoria, String sku) {}

    @DynamicPropertySource
    static void randomDb(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:dompet_sku_conflict_" + UUID.randomUUID() + ";MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
    }

    private ResultActions create(String sku) throws Exception {
        var dto = new Create(
                "Produto X",
                "Descricao",
                new BigDecimal("10.50"),
                5,
                null,
                true,
                "RACAO",
                sku
        );
        return mvc.perform(post("/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto))
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
        );
    }

    @Test
    void deveRetornar409AoCriarProdutoComSkuDuplicado() throws Exception {
        create("SKU-ABC").andExpect(status().isCreated());

        create("SKU-ABC")
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Conflict"))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.type").value(org.hamcrest.Matchers.containsString("/conflict")));
    }
}
