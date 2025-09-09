package com.dompet.api.features.produtos.web;

import com.dompet.api.features.produtos.service.ProdutosService;
import com.dompet.api.features.produtos.dto.ProdutoSuggestionDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProdutosControllerSuggestionsTest {

  MockMvc mvc;

  @Mock
  ProdutosService service;

  @InjectMocks
  ProdutosController controller;

  private void setup() {
    if (mvc == null) {
      mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }
  }

  @Test
  @DisplayName("Deve retornar lista vazia para q curto (<2 chars) sem chamar service")
  void shortQueryReturnsEmpty() throws Exception {
  setup();
  Mockito.when(service.suggestions("a", 8)).thenReturn(List.of());
  mvc.perform(MockMvcRequestBuilders.get("/produtos/suggestions").param("q", "a"))
    .andExpect(MockMvcResultMatchers.status().isOk())
    .andExpect(MockMvcResultMatchers.content().json("[]"));
  }

  @Test
  @DisplayName("Deve retornar sugestões e suportar ETag 304")
  void suggestionsWithEtag() throws Exception {
  setup();
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
