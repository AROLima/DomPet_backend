package com.dompet.api.features.produtos;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProdutosControllerErrorTest {

    @DynamicPropertySource
    static void randomDb(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:dompet_prod_errors_" + UUID.randomUUID() + ";MODE=LEGACY;DB_CLOSE_ON_EXIT=FALSE;DB_CLOSE_DELAY=-1");
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private String toJson(Object o) throws Exception { return objectMapper.writeValueAsString(o); }

    // DTO mínimos para criação (evita depender do pacote de produção caso mude assinatura)
    // Usamos categoria como String para montar JSON; backend espera enum válido (e.g. "RACAO")
    record CreateProduto(String nome, String descricao, java.math.BigDecimal preco, Integer estoque, String imagemUrl, String categoria, Boolean ativo, String sku) {}
    record UpdateProduto(String nome, String descricao, java.math.BigDecimal preco, Integer estoque, String imagemUrl, String categoria, Boolean ativo, String sku) {}

    @Nested
    class ValidationTests {
        @Test
        @DisplayName("POST /produtos -> 400 com errors[] quando payload vazio")
        @WithMockUser(roles = "ADMIN")
        void postProdutoPayloadVazio() throws Exception {
            mockMvc.perform(post("/produtos").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.type", startsWith("https://api.dompet.local/problem/validation-error")))
                .andExpect(jsonPath("$.errors", notNullValue()))
                .andExpect(jsonPath("$.errors", hasSize(greaterThan(0))));
        }

        @Test
        @DisplayName("POST /produtos -> 400 preco negativo")
        @WithMockUser(roles = "ADMIN")
        void postProdutoPrecoNegativo() throws Exception {
            var dto = new CreateProduto("Produto X", "Desc", new java.math.BigDecimal("-10.00"), 5, null, "RACAO", true, null);
            mockMvc.perform(post("/produtos").contentType(MediaType.APPLICATION_JSON).content(toJson(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", notNullValue()))
                .andExpect(jsonPath("$.errors[*].field", hasItem("preco")));
        }

        @Test
        @DisplayName("POST /produtos -> 400 estoque negativo")
        @WithMockUser(roles = "ADMIN")
        void postProdutoEstoqueNegativo() throws Exception {
            var dto = new CreateProduto("Produto Y", "Desc", new java.math.BigDecimal("10.00"), -1, null, "RACAO", true, null);
            mockMvc.perform(post("/produtos").contentType(MediaType.APPLICATION_JSON).content(toJson(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", notNullValue()))
                .andExpect(jsonPath("$.errors[*].field", hasItem("estoque")));
        }
    }

    @Nested
    class SuccessAndConditionalTests {
        @Test
        @DisplayName("POST /produtos -> 201 criado e retorna body com id")
        @WithMockUser(roles = "ADMIN")
        void postProdutoSucesso() throws Exception {
            var dto = new CreateProduto("Produto OK", "Desc", new java.math.BigDecimal("15.50"), 3, null, "RACAO", true, "SKU-OK-1");
            mockMvc.perform(post("/produtos").contentType(MediaType.APPLICATION_JSON).content(toJson(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/produtos/")))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nome").value("Produto OK"));
        }

        @Test
        @DisplayName("PUT /produtos/{id} -> 412 Precondition Failed quando If-Match divergente")
        @WithMockUser(roles = "ADMIN")
        @Transactional
        void putProdutoPreconditionFailed() throws Exception {
            // cria
            var dto = new CreateProduto("Produto ETag", "Desc", new java.math.BigDecimal("20.00"), 2, null, "RACAO", true, "SKU-ETAG");
            var result = mockMvc.perform(post("/produtos").contentType(MediaType.APPLICATION_JSON).content(toJson(dto)))
                .andExpect(status().isCreated())
                .andReturn();
            // extrai id
            var json = result.getResponse().getContentAsString();
            var node = objectMapper.readTree(json);
            long id = node.get("id").asLong();

            // tentativa de update com If-Match incorreto
            var update = new UpdateProduto("Novo Nome", null, new java.math.BigDecimal("25.00"), 2, null, "RACAO", true, null);
            mockMvc.perform(put("/produtos/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("If-Match", "\"hash-invalido\"")
                    .content(toJson(update)))
                .andExpect(status().isPreconditionFailed())
                .andExpect(jsonPath("$.title").value("Precondition Failed"));
        }
    }

    @Nested
    class NotFoundTests {
        @Test
        @DisplayName("GET /produtos/{id} inexistente -> 404 ProblemDetail JSON")
        void getProdutoNotFound() throws Exception {
            mockMvc.perform(get("/produtos/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title", anyOf(is("Not Found"), is("Internal Server Error"))))
                .andExpect(jsonPath("$.type", containsString("problem")))
                .andExpect(jsonPath("$.detail", containsStringIgnoringCase("não")));
        }
    }
}
