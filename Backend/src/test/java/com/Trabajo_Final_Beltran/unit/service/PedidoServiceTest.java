package com.Trabajo_Final_Beltran.unit.service;

import com.Trabajo_Final_Beltran.service.PedidoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link PedidoService}.
 * Uses Mockito for mocking dependencies — no Spring context loaded.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoService - Unit Tests")
class PedidoServiceTest {

    @Test
    @DisplayName("should pass placeholder test")
    void placeholder() {
        assertThat(true).isTrue();
    }
}
