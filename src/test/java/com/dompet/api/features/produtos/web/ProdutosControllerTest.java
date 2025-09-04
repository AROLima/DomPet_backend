package com.dompet.api.features.produtos.web;

import com.dompet.api.features.produtos.domain.Categorias;
import com.dompet.api.features.produtos.dto.ProdutosReadDto;
import com.dompet.api.features.produtos.service.ProdutosService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProdutosController.class,
    excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
    org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
    },
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = com.dompet.api.features.auth.security.JwtAuthFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
@Import(ProdutosControllerTest.MockConfig.class)
class ProdutosControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ProdutosService service;

    @TestConfiguration
    static class MockConfig {
        @Bean
        ProdutosService produtosService() {
            return org.mockito.Mockito.mock(ProdutosService.class);
        }
    }

    @Test
    @DisplayName("GET /produtos deve retornar lista de produtos")
    void listarProdutos_ok() throws Exception {
    var dto = new ProdutosReadDto(1L, "Ração", "Premium", new BigDecimal("10.50"), 5, null, Categorias.RACAO, true, "SKU-1");
        given(service.list(null, null)).willReturn(List.of(dto));

        mvc.perform(get("/produtos").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().exists("ETag"))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Ração"))
                .andExpect(jsonPath("$[0].categoria").value("RACAO"));
    }

    @Test
    @DisplayName("GET /produtos/search deve retornar página de produtos ativos")
    void listarProdutosPaginado_ok() throws Exception {
    var dto = new ProdutosReadDto(1L, "Ração", "Premium", new BigDecimal("10.50"), 5, null, Categorias.RACAO, true, "SKU-1");
        Page<ProdutosReadDto> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 20), 1);
        given(service.search(any(), any(), any())).willReturn(page);

    mvc.perform(get("/produtos/search").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(header().exists("ETag"))
                .andExpect(jsonPath("$.content[0].id").value(1))
        .andExpect(jsonPath("$.content[0].ativo").value(true))
        .andExpect(jsonPath("$.number").value(0))
        .andExpect(jsonPath("$.size").value(20))
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.totalPages").value(1))
        .andExpect(jsonPath("$.first").value(true))
        .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    @DisplayName("GET /produtos/search 304 when If-None-Match matches ETag")
    void listarProdutosPaginado_304_on_match() throws Exception {
    var dto = new ProdutosReadDto(1L, "Ração", "Premium", new BigDecimal("10.50"), 5, null, Categorias.RACAO, true, "SKU-1");
    Page<ProdutosReadDto> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 20), 1);
    given(service.search(any(), any(), any())).willReturn(page);

    var res = mvc.perform(get("/produtos/search").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();
    var etag = res.getResponse().getHeader("ETag");

    // Repeat request with If-None-Match
    mvc.perform(get("/produtos/search").header("If-None-Match", etag).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotModified());
    }

    @Test
    @DisplayName("GET /produtos/{id} deve retornar 404 quando não encontrado")
    void getById_notFound() throws Exception {
        given(service.getById(999L)).willThrow(new EntityNotFoundException("Produto não encontrado"));

        mvc.perform(get("/produtos/999").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
