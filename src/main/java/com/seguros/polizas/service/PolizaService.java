package com.seguros.polizas.service;

import com.seguros.polizas.domain.*;
import com.seguros.polizas.dto.RiesgoRequest;
import com.seguros.polizas.exception.*;
import com.seguros.polizas.repository.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PolizaService {
    private static final String ACTUALIZACION = "ACTUALIZACION";

    private final PolizaRepository polizas;
    private final RiesgoRepository riesgos;
    private final CoreGateway core;
    private final BigDecimal ipc;

    public PolizaService(PolizaRepository polizas, RiesgoRepository riesgos, CoreGateway core,
                         @Value("${app.negocio.ipc}") BigDecimal ipc) {
        this.polizas = polizas; this.riesgos = riesgos; this.core = core; this.ipc = ipc;
    }

    @Transactional(readOnly = true)
    public List<Poliza> listar(TipoPoliza tipo, EstadoPoliza estado) {
        return polizas.buscar(tipo, estado);
    }

    @Transactional(readOnly = true)
    public List<Riesgo> riesgosDePoliza(Long polizaId) {
        obtener(polizaId);
        return riesgos.findByPolizaId(polizaId);
    }

    @Transactional
    public Poliza renovar(Long id) {
        Poliza p = obtener(id);
        if (p.getEstado() == EstadoPoliza.CANCELADA)
            throw new BusinessException("No se puede renovar una póliza cancelada");
        BigDecimal nuevoCanon = p.getCanonMensual().multiply(BigDecimal.ONE.add(ipc))
                .setScale(2, RoundingMode.HALF_UP);
        p.actualizarCanon(nuevoCanon);   // prima = nuevo canon x meses
        p.setEstado(EstadoPoliza.RENOVADA);
        core.notificar(ACTUALIZACION, p.getId());
        return p;
    }

    @Transactional
    public Poliza cancelar(Long id) {
        Poliza p = obtener(id);
        if (p.getEstado() == EstadoPoliza.CANCELADA)
            throw new BusinessException("La póliza ya está cancelada");
        p.setEstado(EstadoPoliza.CANCELADA);
        p.getRiesgos().forEach(r -> r.setEstado(EstadoRiesgo.CANCELADO)); // cancelación en cascada
        core.notificar(ACTUALIZACION, p.getId());
        return p;
    }

    @Transactional
    public Riesgo agregarRiesgo(Long polizaId, RiesgoRequest req) {
        Poliza p = obtener(polizaId);
        if (p.getTipo() != TipoPoliza.COLECTIVA)
            throw new BusinessException("Solo se pueden agregar riesgos a pólizas COLECTIVAS (una individual tiene un único riesgo)");
        if (p.getEstado() == EstadoPoliza.CANCELADA)
            throw new BusinessException("No se pueden agregar riesgos a una póliza cancelada");
        Riesgo r = new Riesgo(req.direccionInmueble(), req.arrendatario(), req.canonMensual());
        p.agregarRiesgo(r);
        riesgos.saveAndFlush(r);
        core.notificar(ACTUALIZACION, p.getId());
        return r;
    }

    @Transactional
    public Riesgo cancelarRiesgo(Long riesgoId) {
        Riesgo r = riesgos.findById(riesgoId)
                .orElseThrow(() -> new NotFoundException("Riesgo no encontrado: " + riesgoId));
        if (r.getEstado() == EstadoRiesgo.CANCELADO)
            throw new BusinessException("El riesgo ya está cancelado");
        r.setEstado(EstadoRiesgo.CANCELADO);
        core.notificar(ACTUALIZACION, r.getPoliza().getId());
        return r;
    }

    private Poliza obtener(Long id) {
        return polizas.findById(id).orElseThrow(() -> new NotFoundException("Póliza no encontrada: " + id));
    }
}
