package com.seguros.polizas.service;

import com.seguros.polizas.dto.CoreEventoRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/** Adaptador: envía el evento al endpoint /core-mock/evento. Un fallo del CORE no revierte la operación. */
@Component
public class CoreMockAdapter implements CoreGateway {
    private static final Logger log = LoggerFactory.getLogger(CoreMockAdapter.class);
    private final RestTemplate rest;
    private final String url;
    private final String apiKey;

    public CoreMockAdapter(RestTemplate rest, @Value("${app.core.url}") String url,
                           @Value("${app.security.api-key}") String apiKey) {
        this.rest = rest; this.url = url; this.apiKey = apiKey;
    }

    @Override
    public void notificar(String evento, Long polizaId) {
        try {
            HttpHeaders h = new HttpHeaders();
            h.setContentType(MediaType.APPLICATION_JSON);
            h.set("x-api-key", apiKey);
            rest.postForEntity(url, new HttpEntity<>(new CoreEventoRequest(evento, polizaId), h), String.class);
        } catch (Exception e) {
            log.error("No fue posible notificar al CORE (evento={}, polizaId={}): {}", evento, polizaId, e.getMessage());
        }
    }
}
