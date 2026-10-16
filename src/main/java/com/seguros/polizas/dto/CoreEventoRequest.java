package com.seguros.polizas.dto;

import jakarta.validation.constraints.*;

public record CoreEventoRequest(@NotBlank String evento, @NotNull Long polizaId) {}
