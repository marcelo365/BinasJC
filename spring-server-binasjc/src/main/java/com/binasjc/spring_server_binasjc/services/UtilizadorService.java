package com.binasjc.spring_server_binasjc.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.binasjc.spring_server_binasjc.model.Utilizador;
import com.binasjc.spring_server_binasjc.repositories.UtilizadorRepository;

@Service
public class UtilizadorService {

    @Autowired
    private UtilizadorRepository repository;

    public Utilizador save(Utilizador utilizador) {
        return repository.save(utilizador);
    }

    public List<Utilizador> getAllUtilizadores() {
        return (List<Utilizador>) repository.findAll();
    }

    public void delete(Utilizador utilizador) {
        repository.delete(utilizador);
    }


    public Utilizador findByUserName(String username) {
        return repository.findByUsername(username);
    }

    public Utilizador findByUserNameAndSenha(String username, String senha) {
        return repository.findByUsernameAndSenha(username , senha);
    }

}
