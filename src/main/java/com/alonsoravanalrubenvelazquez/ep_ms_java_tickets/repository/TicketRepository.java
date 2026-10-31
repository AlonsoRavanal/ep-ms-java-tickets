package com.alonsoravanalrubenvelazquez.ep_ms_java_tickets.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.alonsoravanalrubenvelazquez.ep_ms_java_tickets.model.Ticket;

import jakarta.persistence.LockModeType;

public interface TicketRepository {

    boolean existsByOrdenId(Long ordenId);

    List<Ticket> findByOrdenIdOrderByNumeroEntradaAsc(Long ordenId);

    List<Ticket> findByCompradorIdOrderByIdDesc(Long compradorId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Ticket t WHERE t.codigoAcceso = :codigo")
    Optional<Ticket> buscarPorCodigoParaActualizar(@Param("codigo") String codigo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Ticket t WHERE t.id = :id")
    Optional<Ticket> buscarPorIdParaActualizar(@Param("id") Long id);

}
