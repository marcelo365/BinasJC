package com.binasjc.spring_server_binasjc.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.binasjc.spring_server_binasjc.model.Estacao;
import com.binasjc.spring_server_binasjc.services.EstacaoService;

@RestController
@RequestMapping("/Estacao")
public class EstacaoController {

    @Autowired
    private EstacaoService estacaoService;

    @GetMapping("/get-all")
    public List<Estacao> getAllEstacoes() {
        return estacaoService.getAllEstacoes();
    }

    @GetMapping("/getEstacaoByNome")
    public List<Estacao> getEstacaoByNome(@RequestParam String nome) {
        return estacaoService.findByNome(nome);
    }

    @PostMapping("/save")
    public Estacao saveEstacao(@RequestBody Estacao estacao) {
        return estacaoService.save(estacao);
    }

    @DeleteMapping("/delete")
    public void deleteEstacao(@RequestBody Estacao estacao) {
        estacaoService.delete(estacao);
    }

    @GetMapping("/getEstacaoByID")
    public ResponseEntity<Estacao> getEstacaoByID(@RequestParam int id) {
        Estacao estacao = estacaoService.findByID(id);
        if (estacao == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Retorna 404 se não encontrado
        }
        return ResponseEntity.ok(estacao);
    }

}
