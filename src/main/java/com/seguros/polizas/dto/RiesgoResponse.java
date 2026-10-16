package com.seguros.polizas.dto;

import com.seguros.polizas.domain.*;
import java.math.BigDecimal;

public record RiesgoResponse(Long id, Long polizaId, String direccionInmueble, String arrendatario,
                             BigDecimal canonMensual, EstadoRiesgo estado) {
    public static RiesgoResponse of(Riesgo r) {
        return new RiesgoResponse(r.getId(), r.getPoliza().getId(), r.getDireccionInmueble(),
                r.getArrendatario(), r.getCanonMensual(), r.getEstado());
    }
}
