package com.dompet.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.dompet.api.models.categorias.*;

import jakarta.transaction.Transactional;

@RequestMapping("categorias")
@RestController
public class CategoriasController {

    @Autowired
    private CategoriasRepository repository;

    @Transactional
    @PostMapping
    public void cadastrarCategoria(@RequestBody CategoriasDto dados){
        repository.save(new Categorias(dados));
    }
    



    
}
