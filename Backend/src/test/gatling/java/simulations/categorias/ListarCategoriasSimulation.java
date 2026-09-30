package simulations.categorias;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;


public class ListarCategoriasSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    ScenarioBuilder scn = scenario("Cliente - Listar categorías")
            // Paso 1: Login para obtener el JWT
            .exec(
                    http("POST /auth/login")
                            .post("/auth/login")
                            .body(StringBody(
                                    "{\"email\": \"Alberto@gmail.com\", \"password\": \"Alberto0123\"}"
                            ))
                            .check(status().is(200))
                            .check(jsonPath("$.token").saveAs("jwtToken"))
            )
            .pause(1)
            // Paso 2: Listar categorías con el JWT del cliente
            .exec(
                    http("GET /categorias")
                            .get("/categorias")
                            .header("Authorization", "Bearer #{jwtToken}")
                            .check(status().is(200))
                            // Verifica que la respuesta sea un array JSON
                            .check(jsonPath("$[*]").exists())
            );

    {
        setUp(
                scn.injectOpen(
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
