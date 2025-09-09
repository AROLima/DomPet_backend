package com.dompet.api.features.produtos.web;

import com.dompet.api.features.produtos.service.ProdutosService;
import com.dompet.api.features.produtos.dto.ProdutoSuggestionDto;
import com.dompet.api.features.auth.token.TokenService;
import com.dompet.api.features.usuarios.repo.UsuariosRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@WebMvcTest(controllers = ProdutosController.class,
  excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
class ProdutosControllerSuggestionsTest {

  @Autowired
  MockMvc mvc;

  @MockBean
  ProdutosService service;

  @MockBean
  TokenService tokenService; // mocka dependência do JwtAuthFilter

  @MockBean
  UsuariosRepository usuariosRepository; // mocka dependência do JwtAuthFilter

  @Test
  @DisplayName("Deve retornar lista vazia para q curto (<2 chars) sem chamar service")
  void shortQueryReturnsEmpty() throws Exception {
  Mockito.when(service.suggestions("a", 8)).thenReturn(List.of());
  mvc.perform(MockMvcRequestBuilders.get("/produtos/suggestions").param("q", "a"))
    .andExpect(MockMvcResultMatchers.status().isOk())
    .andExpect(MockMvcResultMatchers.content().json("[]"));
  }

  @Test
  @DisplayName("Deve retornar sugestões e suportar ETag 304")
  void suggestionsWithEtag() throws Exception {
    var list = List.of(new ProdutoSuggestionDto(1L, "Ração X", "img.png", "RACAO-X"));
    Mockito.when(service.suggestions("ra", 8)).thenReturn(list);

    var resp = mvc.perform(MockMvcRequestBuilders.get("/produtos/suggestions").param("q", "ra"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.header().exists("ETag"))
        .andReturn();

    var etag = resp.getResponse().getHeader("ETag");
    mvc.perform(MockMvcRequestBuilders.get("/produtos/suggestions")
            .param("q", "ra")
            .header("If-None-Match", etag))
        .andExpect(MockMvcResultMatchers.status().isNotModified());
  }
}
