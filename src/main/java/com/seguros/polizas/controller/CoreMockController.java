package com.seguros.polizas.controller;

import com.seguros.polizas.dto.CoreEventoRequest;
import jakarta.validation.Valid;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/** Mock del CORE: solo registra en logs que la operación se intentó enviar. */
@RestController
@RequestMapping("/core-mock")
public class CoreMockController {
    private static final Logger log = LoggerFactory.getLogger(CoreMockController.class);

    @PostMapping("/evento")
    public Map<String, String> evento(@Valid @RequestBody CoreEventoRequest req) {
        log.info("[CORE-MOCK] Operación enviada al CORE -> evento={}, polizaId={}", req.evento(), req.polizaId());
        return Map.of("status", "RECIBIDO");
    }
}
