# API de Gestión de Pólizas (Prueba Técnica – Módulo 2)

API REST en **Spring Boot 3 / Java 17** para gestionar pólizas de arrendamiento (Individuales y Colectivas) y sus riesgos. Usa base de datos **H2 en memoria**, por lo que no requiere instalar nada más.

## Qué hace

- Lista pólizas filtrando por tipo y estado, y consulta los riesgos de una póliza.
- Renueva pólizas: sube el canon según el IPC, recalcula la prima (`canon × meses`) y pasa el estado a `RENOVADA`.
- Cancela pólizas (cancela en cascada todos sus riesgos) y cancela riesgos individuales.
- Agrega riesgos solo a pólizas `COLECTIVA`.
- Cada operación que modifica estados notifica al **CORE** mediante `POST /core-mock/evento`, que solo registra en logs el intento de envío.
- Todas las peticiones exigen el header `x-api-key: 123456`.

### Reglas de negocio implementadas
1. Una póliza individual solo tiene 1 riesgo (no admite agregar más).
2. No se puede renovar una póliza cancelada.
3. Cancelar una póliza cancela todos sus riesgos (transaccional).
4. Agregar un riesgo valida que la póliza sea COLECTIVA y no esté cancelada.

## Requisitos
- Java 17+ y Maven 3.8+

## Ejecutar
```bash
mvn spring-boot:run        # arranca en http://localhost:8080
mvn test                   # ejecuta pruebas unitarias
```
Al iniciar se cargan datos de ejemplo: póliza **1** (INDIVIDUAL, 1 riesgo) y póliza **2** (COLECTIVA, 2 riesgos).

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/polizas?tipo=&estado=` | Lista pólizas (filtros opcionales) |
| GET | `/polizas/{id}/riesgos` | Riesgos de una póliza |
| POST | `/polizas/{id}/renovar` | Renueva aplicando IPC |
| POST | `/polizas/{id}/cancelar` | Cancela póliza y sus riesgos |
| POST | `/polizas/{id}/riesgos` | Agrega riesgo (solo COLECTIVA) |
| POST | `/riesgos/{id}/cancelar` | Cancela un riesgo |
| POST | `/core-mock/evento` | Mock del CORE (registra en logs) |

## Ejemplos
```bash
H='x-api-key: 123456'

curl -H "$H" "localhost:8080/polizas?tipo=COLECTIVA&estado=ACTIVA"
curl -H "$H" localhost:8080/polizas/2/riesgos
curl -X POST -H "$H" localhost:8080/polizas/1/renovar

curl -X POST -H "$H" -H 'Content-Type: application/json' \
  -d '{"direccionInmueble":"Cra 9 # 1-1","arrendatario":"Pedro Ruiz","canonMensual":900000}' \
  localhost:8080/polizas/2/riesgos

curl -X POST -H "$H" localhost:8080/riesgos/1/cancelar
curl -X POST -H "$H" localhost:8080/polizas/2/cancelar
```
Agregar un riesgo a la póliza 1 (individual) devuelve `422`. Sin el header responde `401`.
En la consola verá líneas `[CORE-MOCK] Operación enviada al CORE -> evento=ACTUALIZACION, polizaId=...`.

## Configuración (`application.properties`)
- `app.security.api-key`: API key requerida (por defecto `123456`).
- `app.negocio.ipc`: incremento IPC aplicado en renovaciones (por defecto `0.05` = 5%).

## Estructura
```
controller/   Endpoints REST (Poliza, Riesgo, CoreMock)
service/      Reglas de negocio + puerto CoreGateway y su adaptador
repository/   Spring Data JPA
domain/       Entidades Poliza, Riesgo y enums
dto/          Requests/Responses
config/       Filtro x-api-key y datos de ejemplo
exception/    Manejo global de errores (404, 422, 400)
```

## Supuestos
- El IPC es un parámetro configurable (5% por defecto) al no venir definido en el enunciado.
- Códigos de error: `404` no encontrado, `422` regla de negocio violada, `400` datos inválidos, `401` sin API key.
- El CORE se invoca mediante un puerto (`CoreGateway`); si falla, se registra el error y no se revierte la operación (en producción se usaría Outbox + reintentos, ver Módulo 1).
