package com.Trabajo_Final_Beltran.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "blacklist:";

    public void invalidar(String token, long segundosRestantes) {
        try {
            if (segundosRestantes > 0) {
                redisTemplate.opsForValue().set(
                        PREFIX + token,
                        "true",
                        segundosRestantes,
                        TimeUnit.SECONDS
                );
            }
        } catch (Exception e) {
            log.warn("Redis no disponible, el logout no invalida el token: {}", e.getMessage());
        }
    }

    public boolean estaInvalidado(String token) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + token));
        } catch (Exception e) {
            log.warn("Redis no disponible, se omite chequeo de blacklist: {}", e.getMessage());
            return false;
        }
    }
}