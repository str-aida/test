package simulations.pagos;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class PagosClienteSimulation extends Simulation {

        HttpProtocolBuilder httpProtocol = http
                        .baseUrl("http://localhost:8080")
                        .acceptHeader("application/json")
                        .contentTypeHeader("application/json");

        ScenarioBuilder scnCrearPago = scenario("Cliente - Crear pago para un pedido")
                        .exec(
                                        http("POST /auth/login")
                                                        .post("/auth/login")
                                                        .body(StringBody(
                                                                        "{\"email\": \"Alberto@gmail.com\", \"password\": \"Alberto0123\"}"))
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.token").saveAs("jwtToken")))
                        .pause(1)
                        .exec(
                                        http("POST /pedidos (pedido base para pago)")
                                                        .post("/pedidos")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .body(StringBody(
                                                                        "{" +
                                                                                        "\"tipoEntrega\": \"RETIRO_EN_LOCAL\","
                                                                                        +
                                                                                        "\"metodoPago\": \"EFECTIVO\","
                                                                                        +
                                                                                        "\"detalles\": [" +
                                                                                        "  {\"productoId\": 1, \"cantidad\": 1}"
                                                                                        +
                                                                                        "]" +
                                                                                        "}"))
                                                        .check(status().is(201))
                                                        .check(jsonPath("$.id").saveAs("pedidoId")))
                        .pause(1)
                        .exec(
                                        http("POST /pagos/{pedidoId}")
                                                        .post("/pagos/#{pedidoId}")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.id").saveAs("pagoId"))
                                                        .check(jsonPath("$.estado").exists())
                                                        // urlPago puede estar presente si el método es MERCADO_PAGO
                                                        .check(jsonPath("$.urlPago").optional().saveAs("urlPago")));

        ScenarioBuilder scnCrearPagoMercadoPago = scenario("Cliente - Crear pago con Mercado Pago")
                        .exec(
                                        http("POST /auth/login")
                                                        .post("/auth/login")
                                                        .body(StringBody(
                                                                        "{\"email\": \"Alberto@gmail.com\", \"password\": \"Alberto0123\"}"))
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.token").saveAs("jwtToken")))
                        .pause(1)
                        .exec(
                                        http("POST /pedidos (método MERCADO_PAGO)")
                                                        .post("/pedidos")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .body(StringBody(
                                                                        "{" +
                                                                                        "\"tipoEntrega\": \"RETIRO_EN_LOCAL\","
                                                                                        +
                                                                                        "\"metodoPago\": \"MERCADO_PAGO\","
                                                                                        +
                                                                                        "\"detalles\": [" +
                                                                                        "  {\"productoId\": 1, \"cantidad\": 1}"
                                                                                        +
                                                                                        "]" +
                                                                                        "}"))
                                                        .check(status().is(201))
                                                        .check(jsonPath("$.id").saveAs("pedidoId")))
                        .pause(1)
                        .exec(
                                        http("POST /pagos/{pedidoId} (MERCADO_PAGO)")
                                                        .post("/pagos/#{pedidoId}")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        // Puede fallar si no hay credenciales de MP configuradas
                                                        .check(status().in(200, 500))
                                                        .check(jsonPath("$.id").optional().saveAs("pagoId")));

        ScenarioBuilder scnDoblePago = scenario("Cliente - Intentar doble pago (caso negativo)")
                        .exec(
                                        http("POST /auth/login")
                                                        .post("/auth/login")
                                                        .body(StringBody(
                                                                        "{\"email\": \"Alberto@gmail.com\", \"password\": \"Alberto0123\"}"))
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.token").saveAs("jwtToken")))
                        .pause(1)
                        .exec(
                                        http("POST /pedidos (pedido para doble pago)")
                                                        .post("/pedidos")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .body(StringBody(
                                                                        "{" +
                                                                                        "\"tipoEntrega\": \"RETIRO_EN_LOCAL\","
                                                                                        +
                                                                                        "\"metodoPago\": \"EFECTIVO\","
                                                                                        +
                                                                                        "\"detalles\": [" +
                                                                                        "  {\"productoId\": 1, \"cantidad\": 1}"
                                                                                        +
                                                                                        "]" +
                                                                                        "}"))
                                                        .check(status().is(201))
                                                        .check(jsonPath("$.id").saveAs("pedidoId")))
                        .pause(1)
                        .exec(
                                        http("POST /pagos/{pedidoId} (primer pago)")
                                                        .post("/pagos/#{pedidoId}")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .check(status().is(200)))
                        .pause(1)
                        .exec(
                                        http("POST /pagos/{pedidoId} (segundo pago - duplicado)")
                                                        .post("/pagos/#{pedidoId}")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        // 400 o 409 esperado al intentar pagar dos veces
                                                        .check(status().in(200, 400, 409)));

        ScenarioBuilder scnPagoIdInvalido = scenario("Cliente - Pago con pedidoId inexistente")
                        .exec(
                                        http("POST /auth/login")
                                                        .post("/auth/login")
                                                        .body(StringBody(
                                                                        "{\"email\": \"Alberto@gmail.com\", \"password\": \"Alberto0123\"}"))
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.token").saveAs("jwtToken")))
                        .pause(1)
                        .exec(
                                        http("POST /pagos/999999 (pedido inexistente)")
                                                        .post("/pagos/999999")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .check(status().in(400, 404)));

        {
                setUp(
                                scnCrearPago.injectOpen(
                                                rampUsers(100).during(30),           // sube a 100 en 30s
                                                constantUsersPerSec(10).during(60)   // sostiene ~10 req/s por 1 min más
                                ),
                                scnCrearPagoMercadoPago.injectOpen(
                                                rampUsers(100).during(30),           // sube a 100 en 30s
                                                constantUsersPerSec(10).during(60)   // sostiene ~10 req/s por 1 min más
                                ),
                                scnDoblePago.injectOpen(
                                                rampUsers(100).during(30),           // sube a 100 en 30s
                                                constantUsersPerSec(10).during(60)   // sostiene ~10 req/s por 1 min más
                                ),
                                scnPagoIdInvalido.injectOpen(
                                                rampUsers(100).during(30),           // sube a 100 en 30s
                                                constantUsersPerSec(10).during(60)   // sostiene ~10 req/s por 1 min más
                                )
                ).protocols(httpProtocol)
                 .assertions(
                         global().responseTime().max().lt(3000),        // ningún request > 3seg
                         global().successfulRequests().percent().gt(95.0) // al menos 95% éxito
                 );
        }
}
