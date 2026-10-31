package com.alonsoravanalrubenvelazquez.ep_ms_java_tickets.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.alonsoravanalrubenvelazquez.ep_ms_java_tickets.model.Ticket;
import com.alonsoravanalrubenvelazquez.ep_ms_java_tickets.model.dto.EmitirTicketsRequest;
import com.alonsoravanalrubenvelazquez.ep_ms_java_tickets.service.TicketService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tickets")
public class TicketRestController {

    private final TicketService ticketService;

    public TicketRestController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public List<Ticket> listar() {
        return ticketService.findAll();
    }

    @GetMapping("/{id}")
    public Ticket buscar(@PathVariable Long id) {
        return ticketService.findById(id);
    }

    @GetMapping("/comprador/{compradorId}")
    public List<Ticket> porComprador(@PathVariable Long compradorId) {
        return ticketService.findByCompradorId(compradorId);
    }

    @GetMapping("/orden/{ordenId}")
    public List<Ticket> porOrden(@PathVariable Long ordenId) {
        return ticketService.findByOrdenId(ordenId);
    }

    @PostMapping("/emitir")
    public ResponseEntity<List<Ticket>> emitir(@Valid @RequestBody EmitirTicketsRequest solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.emitir(solicitud));
    }

    @PutMapping("/validar/{codigo}")
    public Ticket validar(@PathVariable String codigo) {
        return ticketService.validar(codigo);
    }

    @PutMapping("/{id}/anular")
    public Ticket anular(@PathVariable Long id) {
        return ticketService.anular(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        ticketService.eliminar(id);
    }

}
