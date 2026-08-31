
package com.Trabajo_Final_Beltran.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoConfig {

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @PostConstruct
    public void init() {

        System.out.println(
                "TOKEN MP: " + accessToken
        );

        com.mercadopago.MercadoPagoConfig
                .setAccessToken(
                        accessToken
                );

        System.out.println(
                "Mercado Pago configurado correctamente"
        );
    }
}
