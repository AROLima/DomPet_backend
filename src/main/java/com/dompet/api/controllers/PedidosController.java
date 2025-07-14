package com.dompet.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import com.dompet.api.models.pedidos.*;
import jakarta.transaction.Transactional;


@RestController
@RequestMapping("/pedidos")
public class PedidosController {
    @Autowired
    private PedidosRepository repository;

    @PostMapping
    @Transactional
    public void cadastrarPedido(@RequestBody PedidosDto dados){
        repository.save(new Pedidos(dados));
    }
    
}
