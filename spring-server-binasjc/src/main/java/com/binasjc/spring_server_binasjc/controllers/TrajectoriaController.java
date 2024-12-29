package com.binasjc.spring_server_binasjc.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.binasjc.spring_server_binasjc.model.Trajectoria;
import com.binasjc.spring_server_binasjc.services.TrajectoriaService;

@RestController
@RequestMapping("/Trajectoria")
public class TrajectoriaController {

    @Autowired
    private TrajectoriaService trajectoriaService;

    @PostMapping("/save")
    public Trajectoria saveTrajectoria(@RequestBody Trajectoria trajectoria) {
        return trajectoriaService.save(trajectoria);
    }

    @GetMapping("/getTrajectoriaByIdUsuario")
    public List<Trajectoria> getTrajectoriaByIdUsuario(@RequestParam int idUsuario) {
        return trajectoriaService.findByIdUsuario(idUsuario);
    }

}
