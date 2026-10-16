package com.seguros.polizas.service;

/** Puerto de salida hacia el CORE transaccional (arquitectura hexagonal). */
public interface CoreGateway {
    void notificar(String evento, Long polizaId);
}
