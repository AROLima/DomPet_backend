package com.dompet.api.controllers;

import java.util.List;

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

    @GetMapping
    public List<Pedidos> listarPedidos() {
        return repository.findAll();
    }

    @PutMapping("/{id}")
    @Transactional
    public void atualizarPedido(@PathVariable Long id, @RequestBody PedidosDto dados) {
        var pedido = repository.getReferenceById(id);
        pedido.atualizarInformacoes(dados);
    }

    //exclusão logica
    @DeleteMapping("/{id}")
    @Transactional
    public void excluirPedido(@PathVariable Long id) {
        var pedido = repository.getReferenceById(id);
        pedido.setAtivo(false);
    }

}
