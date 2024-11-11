package com.binasjc.spring_server_binasjc.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.binasjc.spring_server_binasjc.model.Utilizador;


@Repository
public interface UtilizadorRepository extends CrudRepository<Utilizador , Integer> {

    public Utilizador findByUsername(String username);
    public Utilizador findByUsernameAndSenha(String username, String senha);
    
}
