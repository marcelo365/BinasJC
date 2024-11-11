package com.binasjc.spring_server_binasjc.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.binasjc.spring_server_binasjc.model.Bicicleta;
import com.binasjc.spring_server_binasjc.model.Estacao;

@Repository
public interface BicicletaRepository extends CrudRepository<Bicicleta, Integer> {

    List<Bicicleta> findByEstacao(Estacao estacao);

    List<Bicicleta> findByDisponibilidade(int disponibilidade);

    List<Bicicleta> findByDisponibilidadeAndEstacao(int disponibilidade, Estacao estacao);

    List<Bicicleta> findByNome(String nome);

}
