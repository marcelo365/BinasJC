package com.binasjc.spring_server_binasjc.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.binasjc.spring_server_binasjc.model.ReservaBicicleta;
import com.binasjc.spring_server_binasjc.services.ReservaBicicletaService;

@RestController
@RequestMapping("/ReservaBicicleta")
public class ReservaBicicletaController {

    @Autowired
    private ReservaBicicletaService reservaBicicletaService;

    @GetMapping("/get-all")
    public List<ReservaBicicleta> getAllReservasBicicletas() {
        return reservaBicicletaService.getAllReservasBicicletas();
    }

    @GetMapping("/getReservaBicicletaByUsuarioAndEstado")
    public List<ReservaBicicleta> getReservaBicicletaByUsuarioAndEstado(@RequestParam int idUsuario,
            @RequestParam int estado) {
        return reservaBicicletaService.findByIdUsuarioAndEstado(idUsuario, estado);
    }

    @PostMapping("/save")
    public ReservaBicicleta saveReservaBicicleta(@RequestBody ReservaBicicleta reservaBicicleta) {
        return reservaBicicletaService.save(reservaBicicleta);
    }

    @DeleteMapping("/delete")
    public void deleteReservaBicicleta(@RequestBody ReservaBicicleta reservaBicicleta) {
        reservaBicicletaService.delete(reservaBicicleta);
    }

}
