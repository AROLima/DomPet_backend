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
class ProdutosControllerEtagUpdateTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    record Create(String nome, String descricao, BigDecimal preco, Integer estoque, String imagemUrl, String categoria, Boolean ativo, String sku) {}
    record Update(String nome, String descricao, BigDecimal preco, Integer estoque, String imagemUrl, Boolean ativo, String categoria, String sku) {}

    @DynamicPropertySource
    static void randomDb(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:dompet_prod_etag_" + UUID.randomUUID() + ";MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
    }

    private long createOne() throws Exception {
        var dto = new Create("ETag Test", null, new BigDecimal("15.00"), 3, null, "RACAO", true, "SKU-ETAG-1");
        var res = mvc.perform(post("/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto))
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
            .andExpect(status().isCreated())
            .andReturn();
        var json = mapper.readTree(res.getResponse().getContentAsString());
        return json.get("id").asLong();
    }

    private String getEtag(long id) throws Exception {
        var res = mvc.perform(get("/produtos/" + id))
                .andExpect(status().isOk())
                .andReturn();
        return res.getResponse().getHeader("ETag");
    }

    @Test
    void deveAtualizarComIfMatchCorreto() throws Exception {
        long id = createOne();
        String etag = getEtag(id);
        var upd = new Update("ETag Test 2", null, new BigDecimal("20.00"), 4, null, true, "RACAO", "SKU-ETAG-1");
        mvc.perform(put("/produtos/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .header("If-Match", etag)
                .content(mapper.writeValueAsString(upd))
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", org.hamcrest.Matchers.not(etag)))
                .andExpect(jsonPath("$.nome").value("ETag Test 2"))
                .andExpect(jsonPath("$.preco").value(20.00));
    }

    @Test
    void deveRetornar412ComIfMatchIncorreto() throws Exception {
        long id = createOne();
        String etag = getEtag(id);
        var upd = new Update("ETag Test Fail", null, new BigDecimal("30.00"), 10, null, true, "RACAO", "SKU-ETAG-1");
        mvc.perform(put("/produtos/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .header("If-Match", etag + "-outdated")
                .content(mapper.writeValueAsString(upd))
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isPreconditionFailed())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(412))
                .andExpect(jsonPath("$.title").value("Precondition Failed"))
                .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("versão do recurso mudou")));
    }
}
