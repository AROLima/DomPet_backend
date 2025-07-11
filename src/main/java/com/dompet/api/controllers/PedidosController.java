package com.dompet.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.dompet.api.models.pedidos.PedidosRepository;

@RestController
public class PedidosController {
    @Autowired
    private PedidosRepository repository;
}
