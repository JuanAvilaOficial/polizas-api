package com.seguros.polizas.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "riesgos")
public class Riesgo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poliza_id")
    private Poliza poliza;
    @Column(nullable = false)
    private String direccionInmueble;
    @Column(nullable = false)
    private String arrendatario;
    @Column(nullable = false)
    private BigDecimal canonMensual;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private EstadoRiesgo estado = EstadoRiesgo.ACTIVO;

    protected Riesgo() {}

    public Riesgo(String direccionInmueble, String arrendatario, BigDecimal canonMensual) {
        this.direccionInmueble = direccionInmueble;
        this.arrendatario = arrendatario;
        this.canonMensual = canonMensual;
    }

    public Long getId() { return id; }
    public Poliza getPoliza() { return poliza; }
    public void setPoliza(Poliza poliza) { this.poliza = poliza; }
    public String getDireccionInmueble() { return direccionInmueble; }
    public String getArrendatario() { return arrendatario; }
    public BigDecimal getCanonMensual() { return canonMensual; }
    public EstadoRiesgo getEstado() { return estado; }
    public void setEstado(EstadoRiesgo estado) { this.estado = estado; }
}
