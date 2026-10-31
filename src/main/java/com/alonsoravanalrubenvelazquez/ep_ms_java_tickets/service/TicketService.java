package com.alonsoravanalrubenvelazquez.ep_ms_java_tickets.service;

import java.util.List;

import com.alonsoravanalrubenvelazquez.ep_ms_java_tickets.model.Ticket;
import com.alonsoravanalrubenvelazquez.ep_ms_java_tickets.model.dto.EmitirTicketsRequest;

public interface TicketService {
    List<Ticket> findAll();
    Ticket findById(Long id);
    List<Ticket> findByCompradorId(Long compradorId);
    List<Ticket> findByOrdenId(Long ordenId);
    List<Ticket> emitir(EmitirTicketsRequest solicitud);
    Ticket validar(String codigo);
    Ticket anular(Long id);
    void eliminar(Long id);
}
