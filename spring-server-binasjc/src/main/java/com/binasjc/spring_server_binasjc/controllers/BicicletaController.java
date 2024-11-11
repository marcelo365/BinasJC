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
import com.binasjc.spring_server_binasjc.model.Bicicleta;
import com.binasjc.spring_server_binasjc.model.Estacao;
import com.binasjc.spring_server_binasjc.services.BicicletaService;

@RestController
@RequestMapping("/Bicicleta")
public class BicicletaController {

    @Autowired
    private BicicletaService bicicletaService;

    @GetMapping("/get-all")
    public List<Bicicleta> getAllBicicletas() {
        return bicicletaService.getAllBicicletas();
    }

    @PostMapping("/save")
    public Bicicleta saveBicicleta(@RequestBody Bicicleta bicicleta) {
        return bicicletaService.save(bicicleta);
    }

    @DeleteMapping("/delete")
    public void deleteBicicleta(@RequestBody Bicicleta bicicleta) {
        bicicletaService.delete(bicicleta);
    }

    @GetMapping("/getBicicletaByID")
    public ResponseEntity<Bicicleta> getBicicletaByID(@RequestParam int id) {
        Bicicleta bicicleta = bicicletaService.findByID(id);
        if (bicicleta == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Retorna 404 se não encontrado
        }
        return ResponseEntity.ok(bicicleta);
    }

    @GetMapping("/getBicicletaByEstacao")
    public List<Bicicleta> getBicicletaByEstacao(@RequestBody Estacao estacao) {
        return bicicletaService.findByEstacao(estacao);
    }

    @GetMapping("/getBicicletaByNome")
    public List<Bicicleta> getBicicletaByNome(@RequestParam String nome) {
        return bicicletaService.findByNome(nome);
    }

    @GetMapping("/getBicicletasDisponiveisEstacao")
    public List<Bicicleta> getBicicletasDisponiveisEstacao(@RequestBody Estacao estacao) {
        return bicicletaService.getBicicletasDisponiveisEstacao(estacao);
    }

}
