package com.seguros.polizas.controller;

import com.seguros.polizas.dto.RiesgoResponse;
import com.seguros.polizas.service.PolizaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/riesgos")
public class RiesgoController {
    private final PolizaService service;
    public RiesgoController(PolizaService service) { this.service = service; }

    @PostMapping("/{id}/cancelar")
    public RiesgoResponse cancelar(@PathVariable Long id) { return RiesgoResponse.of(service.cancelarRiesgo(id)); }
}
