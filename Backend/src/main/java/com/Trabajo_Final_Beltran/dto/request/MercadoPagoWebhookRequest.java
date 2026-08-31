
package com.Trabajo_Final_Beltran.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MercadoPagoWebhookRequest {
    private String action;

    private String type;

    private Data data;

    @Getter
    @Setter
    public static class Data {

        private String id;
    }
}
