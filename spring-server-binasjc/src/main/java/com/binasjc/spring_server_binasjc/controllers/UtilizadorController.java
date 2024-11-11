package com.binasjc.spring_server_binasjc.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.binasjc.spring_server_binasjc.services.UtilizadorService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.binasjc.spring_server_binasjc.model.Utilizador;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/Utilizador")
public class UtilizadorController {

    @Autowired
    private UtilizadorService utilizadorService;

    @GetMapping("/get-all")
    public List<Utilizador> getAllUtilizadores() {
        return utilizadorService.getAllUtilizadores();
    }

    @GetMapping("/getUtilizadorByUsername")
    public ResponseEntity<Utilizador> getUtilizadorByUsername(@RequestParam String username) {
        Utilizador utilizador = utilizadorService.findByUserName(username);
        if (utilizador == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Retorna 404 se não encontrado
        }
        return ResponseEntity.ok(utilizador);
    }

    @GetMapping("/getUtilizadorByUsernameAndSenha")
    public ResponseEntity<Utilizador> getUtilizadorByUsernameAndSenha(@RequestParam String username , @RequestParam String senha) {
        Utilizador utilizador = utilizadorService.findByUserNameAndSenha(username , senha);
        if (utilizador == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Retorna 404 se não encontrado
        }
        return ResponseEntity.ok(utilizador);
    }

    @PostMapping("/save")
    public Utilizador saveUtilizador(@RequestBody Utilizador utilizador) {
        return utilizadorService.save(utilizador);
    }

    @DeleteMapping("/delete")
    public void deleteUtilizador(@RequestBody Utilizador utilizador) {
        utilizadorService.delete(utilizador);
    }

}
