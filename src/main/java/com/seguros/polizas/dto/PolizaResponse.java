package com.seguros.polizas.dto;

import com.seguros.polizas.domain.*;
import java.math.BigDecimal;

public record PolizaResponse(Long id, TipoPoliza tipo, EstadoPoliza estado, int mesesVigencia,
                             BigDecimal canonMensual, BigDecimal prima) {
    public static PolizaResponse of(Poliza p) {
        return new PolizaResponse(p.getId(), p.getTipo(), p.getEstado(), p.getMesesVigencia(),
                p.getCanonMensual(), p.getPrima());
    }
}
