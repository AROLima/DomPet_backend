package com.dompet.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.dompet.api.models.produtos.*;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/produtos")

public class ProdutosController {
    
    @Autowired 
    private ProdutosRepository repository;

    @PostMapping
    @Transactional
    public void cadastrarProduto(@RequestBody ProdutosDto dados){
        repository.save(new Produtos(dados));
    }

}
