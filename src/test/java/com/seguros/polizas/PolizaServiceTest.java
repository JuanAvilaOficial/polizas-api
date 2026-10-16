package com.seguros.polizas;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.seguros.polizas.domain.*;
import com.seguros.polizas.dto.RiesgoRequest;
import com.seguros.polizas.exception.BusinessException;
import com.seguros.polizas.repository.*;
import com.seguros.polizas.service.*;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PolizaServiceTest {
    PolizaRepository polizas = mock(PolizaRepository.class);
    RiesgoRepository riesgos = mock(RiesgoRepository.class);
    CoreGateway core = mock(CoreGateway.class);
    PolizaService service = new PolizaService(polizas, riesgos, core, new BigDecimal("0.05"));

    @Test
    void renovarIncrementaCanonYPrimaConIpc() {
        Poliza p = new Poliza(TipoPoliza.INDIVIDUAL, 12, new BigDecimal("1000000.00"));
        when(polizas.findById(1L)).thenReturn(Optional.of(p));
        service.renovar(1L);
        assertEquals(0, new BigDecimal("1050000.00").compareTo(p.getCanonMensual()));
        assertEquals(0, new BigDecimal("12600000.00").compareTo(p.getPrima()));
        assertEquals(EstadoPoliza.RENOVADA, p.getEstado());
    }

    @Test
    void noRenuevaPolizaCancelada() {
        Poliza p = new Poliza(TipoPoliza.COLECTIVA, 12, new BigDecimal("1000"));
        p.setEstado(EstadoPoliza.CANCELADA);
        when(polizas.findById(1L)).thenReturn(Optional.of(p));
        assertThrows(BusinessException.class, () -> service.renovar(1L));
    }

    @Test
    void cancelarPolizaCancelaRiesgos() {
        Poliza p = new Poliza(TipoPoliza.COLECTIVA, 12, new BigDecimal("1000"));
        p.agregarRiesgo(new Riesgo("d1", "a1", BigDecimal.TEN));
        p.agregarRiesgo(new Riesgo("d2", "a2", BigDecimal.TEN));
        when(polizas.findById(1L)).thenReturn(Optional.of(p));
        service.cancelar(1L);
        assertTrue(p.getRiesgos().stream().allMatch(r -> r.getEstado() == EstadoRiesgo.CANCELADO));
    }

    @Test
    void noAgregaRiesgoAPolizaIndividual() {
        Poliza p = new Poliza(TipoPoliza.INDIVIDUAL, 12, new BigDecimal("1000"));
        when(polizas.findById(1L)).thenReturn(Optional.of(p));
        assertThrows(BusinessException.class,
                () -> service.agregarRiesgo(1L, new RiesgoRequest("dir", "arr", BigDecimal.TEN)));
    }
}
