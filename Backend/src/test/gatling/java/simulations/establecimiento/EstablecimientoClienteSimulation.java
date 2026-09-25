package simulations.establecimiento;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class EstablecimientoClienteSimulation extends Simulation {

        HttpProtocolBuilder httpProtocol = http
                        .baseUrl("http://localhost:8080")
                        .acceptHeader("application/json")
                        .contentTypeHeader("application/json");

        // GET /establecimiento/info → info pública del establecimiento (isAuthenticated)
        ScenarioBuilder scnInfoEstablecimiento = scenario("Cliente - Obtener info del establecimiento")
                        .exec(
                                        http("POST /auth/login")
                                                        .post("/auth/login")
                                                        .body(StringBody(
                                                                        "{\"email\": \"Alberto@gmail.com\", \"password\": \"Alberto0123\"}"))
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.token").saveAs("jwtToken")))
                        .pause(1)
                        .exec(
                                        http("GET /establecimiento/info")
                                                        .get("/establecimiento/info")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.nombre").exists()));

        // GET /establecimiento/{id}/info → info por id (isAuthenticated)
        ScenarioBuilder scnInfoPorId = scenario("Cliente - Obtener info del establecimiento por ID")
                        .exec(
                                        http("POST /auth/login")
                                                        .post("/auth/login")
                                                        .body(StringBody(
                                                                        "{\"email\": \"Alberto@gmail.com\", \"password\": \"Alberto0123\"}"))
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.token").saveAs("jwtToken")))
                        .pause(1)
                        // Primero obtiene el ID actual
                        .exec(
                                        http("GET /establecimiento/info (obtener id)")
                                                        .get("/establecimiento/info")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.id").saveAs("establecimientoId")))
                        .pause(1)
                        // Luego consulta por ID explícito
                        .exec(
                                        http("GET /establecimiento/{id}/info")
                                                        .get("/establecimiento/#{establecimientoId}/info")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.nombre").exists()));

        {
                setUp(
                        scnInfoEstablecimiento.injectOpen(
                                rampUsers(100).during(30),           // sube a 100 en 30s
                                constantUsersPerSec(10).during(60)   // sostiene ~10 req/s por 1 min más
                        ),
                        scnInfoPorId.injectOpen(
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
