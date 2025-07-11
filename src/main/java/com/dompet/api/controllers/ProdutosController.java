package com.dompet.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.dompet.api.models.produtos.ProdutosRepository;

@RestController

public class ProdutosController {
    
    @Autowired 
    private ProdutosRepository repository;
}
