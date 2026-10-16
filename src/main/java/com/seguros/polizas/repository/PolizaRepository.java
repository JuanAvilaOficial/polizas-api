package com.seguros.polizas.repository;

import com.seguros.polizas.domain.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PolizaRepository extends JpaRepository<Poliza, Long> {
    @Query("select p from Poliza p where (:tipo is null or p.tipo = :tipo) and (:estado is null or p.estado = :estado)")
    List<Poliza> buscar(@Param("tipo") TipoPoliza tipo, @Param("estado") EstadoPoliza estado);
}
