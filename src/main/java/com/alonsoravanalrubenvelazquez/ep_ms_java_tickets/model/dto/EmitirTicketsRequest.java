package com.alonsoravanalrubenvelazquez.ep_ms_java_tickets.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EmitirTicketsRequest(
    @NotNull @Positive Long ordenId,
    @NotNull @Positive Long eventoId,
    @NotNull @Positive Long compradorId,
    @NotNull @Positive Integer cantidad
) {

}
