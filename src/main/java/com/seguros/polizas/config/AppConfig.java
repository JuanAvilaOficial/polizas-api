package com.seguros.polizas.config;

import com.seguros.polizas.domain.*;
import com.seguros.polizas.repository.PolizaRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Bean
    RestTemplate restTemplate() { return new RestTemplate(); }

    /** Datos de ejemplo para probar rápidamente. */
    @Bean
    CommandLineRunner seed(PolizaRepository repo) {
        return args -> {
            Poliza ind = new Poliza(TipoPoliza.INDIVIDUAL, 12, new BigDecimal("1000000.00"));
            ind.agregarRiesgo(new Riesgo("Calle 10 # 5-20", "Juan Pérez", new BigDecimal("1000000.00")));
            repo.save(ind);

            Poliza col = new Poliza(TipoPoliza.COLECTIVA, 12, new BigDecimal("2500000.00"));
            col.agregarRiesgo(new Riesgo("Cra 7 # 45-10 Apto 101", "Ana Gómez", new BigDecimal("1200000.00")));
            col.agregarRiesgo(new Riesgo("Cra 7 # 45-10 Apto 202", "Luis Mora", new BigDecimal("1300000.00")));
            repo.save(col);
        };
    }
}
