package com.logistics.packages.application.usecase;

import com.logistics.packages.application.ports.SedeRepository;
import com.logistics.packages.application.repository.DistanceService;
import com.logistics.packages.application.repository.PaqueteRepository;
import com.logistics.packages.domain.exception.PaqueteNotFoundException;
import com.logistics.packages.domain.model.Paquete;
import com.logistics.packages.domain.model.Sede;
import com.logistics.packages.domain.valueobject.Coordenadas;
import com.logistics.packages.domain.valueobject.EstadoGps;
import com.logistics.packages.domain.valueobject.EstadoPaquete;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ActualizarCoordenadasUseCase (contingencia GPS)")
class ActualizarCoordenadasUseCaseTest {

    @Mock private PaqueteRepository paqueteRepository;
    @Mock private SedeRepository sedeRepository;
    @Mock private DistanceService distanceService;

    @InjectMocks private ActualizarCoordenadasUseCase useCase;

    private final UUID paqueteId = UUID.randomUUID();
    private final UUID sedeId = UUID.randomUUID();
    private final Coordenadas destino = new Coordenadas(4.6765, -74.0482);

    private Paquete paquetePendiente() {
        return Paquete.builder()
                .id(paqueteId)
                .sedeId(sedeId)
                .estado(EstadoPaquete.RECIBIDO_EN_SEDE)
                .estadoGps(EstadoGps.PENDIENTE)
                .build();
    }

    @Test
    @DisplayName("Resuelve el GPS y recalcula la distancia desde la sede")
    void resuelveGpsYRecalculaDistancia() {
        Paquete paquete = paquetePendiente();
        Sede sede = Sede.builder().id(sedeId).latitud(4.624335).longitud(-74.063644).build();
        when(paqueteRepository.findById(paqueteId)).thenReturn(Optional.of(paquete));
        when(sedeRepository.findById(sedeId)).thenReturn(Optional.of(sede));
        when(distanceService.calcularDistanciaDesdeSede(any(Coordenadas.class), eq(destino))).thenReturn(6.3);

        Paquete resultado = useCase.actualizar(paqueteId, destino);

        assertEquals(EstadoGps.RESUELTO, resultado.getEstadoGps());
        assertEquals(destino, resultado.getCoordenadas());
        assertEquals(6.3, resultado.getDistanciaEstimadaKm());
        verify(paqueteRepository).save(paquete);
    }

    @Test
    @DisplayName("Si la sede no tiene coordenadas, resuelve el GPS sin recalcular distancia")
    void sedeSinCoordenadasNoRecalcula() {
        Paquete paquete = paquetePendiente();
        when(paqueteRepository.findById(paqueteId)).thenReturn(Optional.of(paquete));
        when(sedeRepository.findById(sedeId)).thenReturn(Optional.of(Sede.builder().id(sedeId).build()));

        Paquete resultado = useCase.actualizar(paqueteId, destino);

        assertEquals(EstadoGps.RESUELTO, resultado.getEstadoGps());
        assertNull(resultado.getDistanciaEstimadaKm());
        verify(distanceService, never()).calcularDistanciaDesdeSede(any(), any());
    }

    @Test
    @DisplayName("Lanza PaqueteNotFoundException si el paquete no existe")
    void paqueteInexistente() {
        when(paqueteRepository.findById(paqueteId)).thenReturn(Optional.empty());

        assertThrows(PaqueteNotFoundException.class, () -> useCase.actualizar(paqueteId, destino));
        verify(paqueteRepository, never()).save(any());
    }
}
