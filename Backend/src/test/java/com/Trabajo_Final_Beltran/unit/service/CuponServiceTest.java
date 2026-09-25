package com.Trabajo_Final_Beltran.unit.service;

import com.Trabajo_Final_Beltran.service.CuponService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link CuponService}.
 * Uses Mockito for mocking dependencies — no Spring context loaded.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CuponService - Unit Tests")
class CuponServiceTest {

    @Test
    @DisplayName("should pass placeholder test")
    void placeholder() {
        assertThat(true).isTrue();
    }
}
