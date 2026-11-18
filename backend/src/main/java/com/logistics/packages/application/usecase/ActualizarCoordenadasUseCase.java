package com.logistics.packages.application.usecase;

import com.logistics.packages.application.ports.SedeRepository;
import com.logistics.packages.application.repository.DistanceService;
import com.logistics.packages.application.repository.PaqueteRepository;
import com.logistics.packages.domain.exception.PaqueteNotFoundException;
import com.logistics.packages.domain.model.Paquete;
import com.logistics.packages.domain.valueobject.Coordenadas;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * UC-001 FR-009: ingreso manual de coordenadas como contingencia ante fallo de GPS.
 *
 * Además de resolver el GPS, recalcula la distancia desde la sede de origen para que
 * el pesaje posterior calcule el precio con la distancia real.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ActualizarCoordenadasUseCase {

    private final PaqueteRepository paqueteRepository;
    private final SedeRepository sedeRepository;
    private final DistanceService distanceService;

    public Paquete actualizar(UUID paqueteId, Coordenadas coordenadas) {
        Paquete paquete = paqueteRepository.findById(paqueteId)
                .orElseThrow(() -> new PaqueteNotFoundException(paqueteId));

        paquete.asignarCoordenadas(coordenadas);

        if (paquete.getSedeId() != null) {
            sedeRepository.findById(paquete.getSedeId())
                    .filter(sede -> sede.getLatitud() != null && sede.getLongitud() != null)
                    .ifPresentOrElse(
                            sede -> paquete.asignarDistanciaEstimada(distanceService.calcularDistanciaDesdeSede(
                                    new Coordenadas(sede.getLatitud(), sede.getLongitud()), coordenadas)),
                            () -> log.warn("Sede {} sin coordenadas: no se recalcula la distancia del paquete {}",
                                    paquete.getSedeId(), paqueteId));
        }

        paqueteRepository.save(paquete);
        log.info("Coordenadas actualizadas para paquete {}. Distancia estimada: {} km",
                paqueteId, paquete.getDistanciaEstimadaKm());
        return paquete;
    }
}
