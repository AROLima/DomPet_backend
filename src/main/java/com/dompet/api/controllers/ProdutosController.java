package com.dompet.api.controllers;

import java.util.List;

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

    @GetMapping
    public List<Produtos> listarProdutos() {
        return repository.findAll();
    }
    
    @PutMapping("/{id}")
    @Transactional
    public void atualizarProduto(@PathVariable Long id, @RequestBody ProdutosDto dados) {
        var produto = repository.getReferenceById(id);
        produto.atualizarInformacoes(dados);
    }

    //exclusão logica
    @DeleteMapping("/{id}")
    @Transactional
    public void excluirProduto(@PathVariable Long id) {
        var produto = repository.getReferenceById(id);
        produto.setAtivo(false);
    }



}