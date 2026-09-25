package simulations.notificaciones;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class NotificacionesClienteSimulation extends Simulation {

        HttpProtocolBuilder httpProtocol = http
                        .baseUrl("http://localhost:8080")
                        .acceptHeader("application/json")
                        .contentTypeHeader("application/json");

        ScenarioBuilder scnListarNotificaciones = scenario("Cliente - Listar mis notificaciones")
                        .exec(
                                        http("POST /auth/login")
                                                        .post("/auth/login")
                                                        .body(StringBody(
                                                                        "{\"email\": \"Alberto@gmail.com\", \"password\": \"Alberto0123\"}"))
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.token").saveAs("jwtToken")))
                        .pause(1)
                        .exec(
                                        http("GET /notificaciones")
                                                        .get("/notificaciones")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .check(status().is(200))
                                                        .check(bodyString().exists()));

        ScenarioBuilder scnContarNoLeidas = scenario("Cliente - Contar notificaciones no leídas")
                        .exec(
                                        http("POST /auth/login")
                                                        .post("/auth/login")
                                                        .body(StringBody(
                                                                        "{\"email\": \"Alberto@gmail.com\", \"password\": \"Alberto0123\"}"))
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.token").saveAs("jwtToken")))
                        .pause(1)
                        .exec(
                                        http("GET /notificaciones/no-leidas (contador)")
                                                        .get("/notificaciones/no-leidas")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .check(status().is(200)));

        ScenarioBuilder scnMarcarLeida = scenario("Cliente - Marcar notificación como leída")
                        .exec(
                                        http("POST /auth/login")
                                                        .post("/auth/login")
                                                        .body(StringBody(
                                                                        "{\"email\": \"Alberto@gmail.com\", \"password\": \"Alberto0123\"}"))
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.token").saveAs("jwtToken")))
                        .pause(1)
                        // Obtener la primera notificación disponible
                        .exec(
                                        http("GET /notificaciones (obtener id)")
                                                        .get("/notificaciones")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .check(status().is(200))
                                                        .check(jsonPath("$[0].id").optional().saveAs("notifId")))
                        .pause(1)
                        // Marcar como leída solo si existe alguna
                        .doIf(session -> session.contains("notifId") && session.getString("notifId") != null)
                        .then(
                                        exec(
                                                        http("PATCH /notificaciones/{id}/leida")
                                                                        .patch("/notificaciones/#{notifId}/leida")
                                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                                        .check(status().is(204))));

        ScenarioBuilder scnMarcarTodasLeidas = scenario("Cliente - Marcar todas las notificaciones como leídas")
                        .exec(
                                        http("POST /auth/login")
                                                        .post("/auth/login")
                                                        .body(StringBody(
                                                                        "{\"email\": \"Alberto@gmail.com\", \"password\": \"Alberto0123\"}"))
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.token").saveAs("jwtToken")))
                        .pause(1)
                        .exec(
                                        http("PATCH /notificaciones/marcar-todas-leidas")
                                                        .patch("/notificaciones/marcar-todas-leidas")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .check(status().is(204)));

        {
                setUp(
                                scnListarNotificaciones.injectOpen(
                                                rampUsers(100).during(30),           // sube a 100 en 30s
                                                constantUsersPerSec(10).during(60)   // sostiene ~10 req/s por 1 min más
                                ),
                                scnContarNoLeidas.injectOpen(
                                                rampUsers(100).during(30),           // sube a 100 en 30s
                                                constantUsersPerSec(10).during(60)   // sostiene ~10 req/s por 1 min más
                                ),
                                scnMarcarLeida.injectOpen(
                                                rampUsers(100).during(30),           // sube a 100 en 30s
                                                constantUsersPerSec(10).during(60)   // sostiene ~10 req/s por 1 min más
                                ),
                                scnMarcarTodasLeidas.injectOpen(
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
