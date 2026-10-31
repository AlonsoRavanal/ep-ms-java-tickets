package com.alonsoravanalrubenvelazquez.ep_ms_java_tickets.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.alonsoravanalrubenvelazquez.ep_ms_java_tickets.model.Ticket;
import com.alonsoravanalrubenvelazquez.ep_ms_java_tickets.model.Ticket.EstadoTicket;
import com.alonsoravanalrubenvelazquez.ep_ms_java_tickets.model.dto.EmitirTicketsRequest;
import com.alonsoravanalrubenvelazquez.ep_ms_java_tickets.repository.TicketRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Ticket findById(Long id) {
        return ticketRepository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket no encontrado: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> findByCompradorId(Long compradorId) {
        return ticketRepository.findByCompradorIdOrderByIdDesc(compradorId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> findByOrdenId(Long ordenId) {
        return ticketRepository.findByOrdenIdOrderByNumeroEntradaAsc(ordenId);
    }

    @Override
    @Transactional
    public List<Ticket> emitir(EmitirTicketsRequest solicitud) {
        if (solicitud == null || solicitud.ordenId() == null || solicitud.ordenId() <= 0 ||
            solicitud.eventoId() == null || solicitud.eventoId() <= 0 ||
            solicitud.compradorId() == null || solicitud.compradorId() <= 0 ||
            solicitud.cantidad() == null || solicitud.cantidad() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos de emisión inválidos");
        }

        if (ticketRepository.existsByOrdenId(solicitud.ordenId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "La orden ya tiene tickets emitidos: " + solicitud.ordenId());
        }

        List<Ticket> nuevos = new ArrayList<>();
        for (int numero = 1; numero <= solicitud.cantidad(); numero++) {
            nuevos.add(new Ticket(
                solicitud.ordenId(),
                solicitud.eventoId(),
                solicitud.compradorId(),
                numero
            ));
        }
        return ticketRepository.saveAll(nuevos);
    }

    @Override
    @Transactional
    public Ticket validar(String codigo) {
        Ticket ticket = ticketRepository.buscarPorCodigoParaActualizar(codigo)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Código de acceso no encontrado"));

        if (ticket.getEstado() != EstadoTicket.EMITIDO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "El código ya fue utilizado o anulado");
        }

        ticket.marcarUtilizado();
        return ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public Ticket anular(Long id) {
        Ticket ticket = ticketRepository.buscarPorIdParaActualizar(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Ticket no encontrado: " + id));

        if (ticket.getEstado() != EstadoTicket.EMITIDO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Solo pueden anularse tickets emitidos y no utilizados");
        }

        ticket.anular();
        return ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Ticket ticket = ticketRepository.buscarPorIdParaActualizar(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Ticket no encontrado: " + id));

        if (ticket.getEstado() != EstadoTicket.EMITIDO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "No se puede eliminar un ticket utilizado o anulado");
        }
        ticketRepository.delete(ticket);
    }
}
