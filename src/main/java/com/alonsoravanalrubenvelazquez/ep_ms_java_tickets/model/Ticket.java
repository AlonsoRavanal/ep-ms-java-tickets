package com.alonsoravanalrubenvelazquez.ep_ms_java_tickets.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table (name = "tickets", uniqueConstraints = {
    @UniqueConstraint(name = "uk_ticket_orden_numero", columnNames = {"orden_id", "numero_entrada"})
})
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "orden_id", nullable = false)
    private Long ordenId;

    @Column(name = "evento_id", nullable = false)
    private Long eventoId;

    @Column(name = "comprador_id", nullable = false)
    private Long compradorId;

    @Column(name = "numero_entrada", nullable = false)
    private Integer numeroEntrada;

    @Column(name = "codigo_acceso", nullable = false, unique = true, updatable = false, length = 36)
    private String codigoAcceso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoTicket estado;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaEmision;

    private LocalDateTime fechaUso;

    public enum EstadoTicket {
        EMITIDO,
        UTILIZADO,
        ANULADO
    }

    public Ticket() {
    }

    public Ticket(Long ordenId, Long eventoId, Long compradorId, Integer numeroEntrada) {
        this.ordenId = ordenId;
        this.eventoId = eventoId;
        this.compradorId = compradorId;
        this.numeroEntrada = numeroEntrada;
    }

    @PrePersist
    void inicializar() {
        if (codigoAcceso == null) codigoAcceso = UUID.randomUUID().toString();
        if (fechaEmision == null) fechaEmision = LocalDateTime.now();
        if (estado == null) estado = EstadoTicket.EMITIDO;
    }

    public Long getId() {
        return id;
    }

    public void marcarUtilizado() {
        this.estado = EstadoTicket.UTILIZADO;
        this.fechaUso = LocalDateTime.now();
    }

    public void anular() {
        this.estado = EstadoTicket.ANULADO;
    }

    public Long getOrdenId() {
        return ordenId;
    }

    public void setOrdenId(Long ordenId) {
        this.ordenId = ordenId;
    }

    public Long getEventoId() {
        return eventoId;
    }

    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }

    public Long getCompradorId() {
        return compradorId;
    }

    public void setCompradorId(Long compradorId) {
        this.compradorId = compradorId;
    }

    public Integer getNumeroEntrada() {
        return numeroEntrada;
    }

    public void setNumeroEntrada(Integer numeroEntrada) {
        this.numeroEntrada = numeroEntrada;
    }

    public String getCodigoAcceso() {
        return codigoAcceso;
    }

    public void setCodigoAcceso(String codigoAcceso) {
        this.codigoAcceso = codigoAcceso;
    }

    public EstadoTicket getEstado() {
        return estado;
    }

    public void setEstado(EstadoTicket estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public LocalDateTime getFechaUso() {
        return fechaUso;
    }

    public void setFechaUso(LocalDateTime fechaUso) {
        this.fechaUso = fechaUso;
    }

}
