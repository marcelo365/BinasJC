package com.binasjc.spring_server_binasjc.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.binasjc.spring_server_binasjc.model.Bicicleta;

@Repository
public interface BicicletaRepository extends CrudRepository<Bicicleta, Integer> {

    @Query("SELECT b FROM Bicicleta b WHERE b.estacao.id = :idEstacao")
    List<Bicicleta> findByIdEstacao(int idEstacao);

    List<Bicicleta> findByDisponibilidade(int disponibilidade);

    @Query("SELECT b FROM Bicicleta b WHERE b.disponibilidade = :disponibilidade AND b.estacao.id = :idEstacao")
    List<Bicicleta> findByDisponibilidadeAndIdEstacao(int disponibilidade, int idEstacao);

    List<Bicicleta> findByNome(String nome);

}
