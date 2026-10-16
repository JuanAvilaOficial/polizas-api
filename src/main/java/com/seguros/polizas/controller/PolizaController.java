package com.seguros.polizas.controller;

import com.seguros.polizas.domain.*;
import com.seguros.polizas.dto.*;
import com.seguros.polizas.service.PolizaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/polizas")
public class PolizaController {
    private final PolizaService service;
    public PolizaController(PolizaService service) { this.service = service; }

    @GetMapping
    public List<PolizaResponse> listar(@RequestParam(required = false) TipoPoliza tipo,
                                       @RequestParam(required = false) EstadoPoliza estado) {
        return service.listar(tipo, estado).stream().map(PolizaResponse::of).toList();
    }

    @GetMapping("/{id}/riesgos")
    public List<RiesgoResponse> riesgos(@PathVariable Long id) {
        return service.riesgosDePoliza(id).stream().map(RiesgoResponse::of).toList();
    }

    @PostMapping("/{id}/renovar")
    public PolizaResponse renovar(@PathVariable Long id) { return PolizaResponse.of(service.renovar(id)); }

    @PostMapping("/{id}/cancelar")
    public PolizaResponse cancelar(@PathVariable Long id) { return PolizaResponse.of(service.cancelar(id)); }

    @PostMapping("/{id}/riesgos")
    public ResponseEntity<RiesgoResponse> agregarRiesgo(@PathVariable Long id, @Valid @RequestBody RiesgoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(RiesgoResponse.of(service.agregarRiesgo(id, req)));
    }
}
