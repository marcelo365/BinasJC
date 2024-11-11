package com.binasjc.spring_server_binasjc.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.binasjc.spring_server_binasjc.model.Estacao;

@Repository
public interface EstacaoRepository extends CrudRepository< Estacao , Integer> {

    List<Estacao> findByNome(String nome);

    
}
