package com.binasjc.spring_server_binasjc.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.binasjc.spring_server_binasjc.model.ReservaBicicleta;

@Repository
public interface ReservaBicicletaRepository extends CrudRepository<ReservaBicicleta, Integer> {

     @Query("SELECT r FROM ReservaBicicleta r WHERE r.usuario.id = :idUsuario AND r.estado = :estado")
     public List<ReservaBicicleta> findByIdUsuarioAndEstado(int idUsuario, int estado);
}
