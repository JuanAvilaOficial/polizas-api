package com.seguros.polizas.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record RiesgoRequest(@NotBlank String direccionInmueble,
                            @NotBlank String arrendatario,
                            @NotNull @Positive BigDecimal canonMensual) {}
