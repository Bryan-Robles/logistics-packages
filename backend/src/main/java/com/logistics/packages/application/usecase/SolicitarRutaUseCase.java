package com.logistics.packages.application.usecase;

import com.logistics.packages.application.repository.PaqueteRepository;
import com.logistics.packages.application.ports.RutaQueuePort;
import com.logistics.packages.domain.event.SolicitudRutaEvent;
import com.logistics.packages.domain.exception.PaqueteNotFoundException;
import com.logistics.packages.domain.model.Paquete;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolicitarRutaUseCase {

    private final PaqueteRepository paqueteRepository;
    private final RutaQueuePort rutaQueuePort;

    public void handle(SolicitudRutaEvent event) {
        log.info("Procesando solicitud de ruta para paquete: {}", event.getPaqueteId());

        Paquete paquete = paqueteRepository.findById(event.getPaqueteId())
                .orElseThrow(() -> new PaqueteNotFoundException(event.getPaqueteId()));

        // UC-001: solicitar_ruta queda bloqueado hasta resolver las coordenadas (GPS PENDIENTE)
        if (paquete.getCoordenadas() == null) {
            log.warn("Paquete {} sin coordenadas (GPS pendiente): no se solicita ruta todavía", paquete.getId());
            return;
        }

        log.info("Enviando solicitud de ruta para paquete {} con peso {}kg, volumen {}m3",
                paquete.getId(),
                paquete.getPeso() != null ? paquete.getPeso().getKilogramos() : null,
                paquete.getVolumenM3());

        rutaQueuePort.enviarSolicitud(paquete);

        log.info("Solicitud de ruta enviada exitosamente para paquete: {}", event.getPaqueteId());
    }
}
