package com.seguros.polizas.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "polizas")
public class Poliza {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private TipoPoliza tipo;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private EstadoPoliza estado = EstadoPoliza.ACTIVA;
    @Column(nullable = false)
    private int mesesVigencia;
    @Column(nullable = false)
    private BigDecimal canonMensual;
    @Column(nullable = false)
    private BigDecimal prima;
    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL)
    private List<Riesgo> riesgos = new ArrayList<>();

    protected Poliza() {}

    public Poliza(TipoPoliza tipo, int mesesVigencia, BigDecimal canonMensual) {
        this.tipo = tipo;
        this.mesesVigencia = mesesVigencia;
        this.canonMensual = canonMensual;
        this.prima = canonMensual.multiply(BigDecimal.valueOf(mesesVigencia));
    }

    /** Regla: prima = canon mensual x meses de vigencia. */
    public void actualizarCanon(BigDecimal nuevoCanon) {
        this.canonMensual = nuevoCanon;
        this.prima = nuevoCanon.multiply(BigDecimal.valueOf(mesesVigencia));
    }

    public void agregarRiesgo(Riesgo r) { riesgos.add(r); r.setPoliza(this); }

    public Long getId() { return id; }
    public TipoPoliza getTipo() { return tipo; }
    public EstadoPoliza getEstado() { return estado; }
    public void setEstado(EstadoPoliza estado) { this.estado = estado; }
    public int getMesesVigencia() { return mesesVigencia; }
    public BigDecimal getCanonMensual() { return canonMensual; }
    public BigDecimal getPrima() { return prima; }
    public List<Riesgo> getRiesgos() { return riesgos; }
}
