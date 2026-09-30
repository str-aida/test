package simulations.cupones;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class CuponesClienteSimulation extends Simulation {

        HttpProtocolBuilder httpProtocol = http
                        .baseUrl("http://localhost:8080")
                        .acceptHeader("application/json")
                        .contentTypeHeader("application/json");

        ScenarioBuilder scnMisCupones = scenario("Cliente - Listar mis cupones")
                        .exec(
                                        http("POST /auth/login")
                                                        .post("/auth/login")
                                                        .body(StringBody(
                                                                        "{\"email\": \"Alberto@gmail.com\", \"password\": \"Alberto0123\"}"))
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.token").saveAs("jwtToken")))
                        .pause(1)
                        .exec(
                                        http("GET /cupones/mis-cupones")
                                                        .get("/cupones/mis-cupones")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .check(status().is(200))
                                                        .check(bodyString().exists()));

        ScenarioBuilder scnInspeccionarCupon = scenario("Cliente - Inspeccionar primer cupón disponible")
                        .exec(
                                        http("POST /auth/login")
                                                        .post("/auth/login")
                                                        .body(StringBody(
                                                                        "{\"email\": \"Alberto@gmail.com\", \"password\": \"Alberto0123\"}"))
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.token").saveAs("jwtToken")))
                        .pause(1)
                        .exec(
                                        http("GET /cupones/mis-cupones (inspeccionar primero)")
                                                        .get("/cupones/mis-cupones")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .check(status().is(200))
                                                        .check(
                                                                        jsonPath("$[0].cupon.codigo")
                                                                                        .optional()
                                                                                        .saveAs("codigoCupon"))
                                                        .check(
                                                                        jsonPath("$[0].id")
                                                                                        .optional()
                                                                                        .saveAs("cuponUsuarioId")));

        ScenarioBuilder scnAplicarCupon = scenario("Cliente - Crear pedido y aplicar cupón")
                        .exec(
                                        http("POST /auth/login")
                                                        .post("/auth/login")
                                                        .body(StringBody(
                                                                        "{\"email\": \"Alberto@gmail.com\", \"password\": \"Alberto0123\"}"))
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.token").saveAs("jwtToken")))
                        .pause(1)
                        .exec(
                                        http("POST /pedidos (crear pedido para aplicar cupón)")
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
                                        http("GET /cupones/mis-cupones (obtener código)")
                                                        .get("/cupones/mis-cupones")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .check(status().is(200))
                                                        .check(
                                                                        jsonPath("$[?(@.usado == false)][0].cupon.codigo")
                                                                                        .optional()
                                                                                        .saveAs("codigoCupon")))
                        .pause(1)
                        .doIf(session -> session.contains("codigoCupon") && session.getString("codigoCupon") != null)
                        .then(
                                        exec(
                                                        http("PUT /pedidos/{id}/aplicar-cupon")
                                                                        .put("/pedidos/#{pedidoId}/aplicar-cupon")
                                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                                        .body(StringBody(
                                                                                        "{" +
                                                                                                        "\"codigo\": \"#{codigoCupon}\","
                                                                                                        +
                                                                                                        "\"pedidoId\": #{pedidoId}"
                                                                                                        +
                                                                                                        "}"))
                                                                        .check(status().in(200, 400))
                                                                        .check(jsonPath("$.valido").optional()
                                                                                        .saveAs("cuponValido"))));

        ScenarioBuilder scnCuponInvalido = scenario("Cliente - Aplicar cupón con código inválido")
                        .exec(
                                        http("POST /auth/login")
                                                        .post("/auth/login")
                                                        .body(StringBody(
                                                                        "{\"email\": \"Alberto@gmail.com\", \"password\": \"Alberto0123\"}"))
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.token").saveAs("jwtToken")))
                        .pause(1)
                        .exec(
                                        http("POST /pedidos (pedido para cupón inválido)")
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
                                        http("PUT /pedidos/{id}/aplicar-cupon (código inválido)")
                                                        .put("/pedidos/#{pedidoId}/aplicar-cupon")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .body(StringBody(
                                                                        "{" +
                                                                                        "\"codigo\": \"CODIGO_INEXISTENTE_XYZ\","
                                                                                        +
                                                                                        "\"pedidoId\": #{pedidoId}" +
                                                                                        "}"))
                                                        .check(status().in(200, 400, 404)));

        {
                setUp(
                                scnMisCupones.injectOpen(
                                                rampUsers(100).during(30),           // sube a 100 en 30s
                                                constantUsersPerSec(10).during(60)   // sostiene ~10 req/s por 1 min más
                                ),
                                scnInspeccionarCupon.injectOpen(
                                                rampUsers(100).during(30),           // sube a 100 en 30s
                                                constantUsersPerSec(10).during(60)   // sostiene ~10 req/s por 1 min más
                                ),
                                scnAplicarCupon.injectOpen(
                                                rampUsers(100).during(30),           // sube a 100 en 30s
                                                constantUsersPerSec(10).during(60)   // sostiene ~10 req/s por 1 min más
                                ),
                                scnCuponInvalido.injectOpen(
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
