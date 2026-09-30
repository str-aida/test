package simulations.direcciones;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class DireccionesClienteSimulation extends Simulation {

        HttpProtocolBuilder httpProtocol = http
                        .baseUrl("http://localhost:8080")
                        .acceptHeader("application/json")
                        .contentTypeHeader("application/json");

        ScenarioBuilder scnListarDirecciones = scenario("Cliente - Listar mis direcciones")
                        .exec(
                                        http("POST /auth/login")
                                                        .post("/auth/login")
                                                        .body(StringBody(
                                                                        "{\"email\": \"Alberto@gmail.com\", \"password\": \"Alberto0123\"}"))
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.token").saveAs("jwtToken")))
                        .pause(1)
                        .exec(
                                        http("GET /direcciones")
                                                        .get("/direcciones")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .check(status().is(200))
                                                        .check(bodyString().exists()));

        ScenarioBuilder scnCrearDireccion = scenario("Cliente - Crear nueva dirección")
                        .exec(
                                        http("POST /auth/login")
                                                        .post("/auth/login")
                                                        .body(StringBody(
                                                                        "{\"email\": \"Alberto@gmail.com\", \"password\": \"Alberto0123\"}"))
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.token").saveAs("jwtToken")))
                        .pause(1)
                        .exec(
                                        http("POST /direcciones")
                                                        .post("/direcciones")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .body(StringBody(
                                                                        "{" +
                                                                                        "\"nombre\": \"Casa\"," +
                                                                                        "\"calle\": \"Av. Siempre Viva\"," +
                                                                                        "\"numero\": \"742\"," +
                                                                                        "\"localidad\": \"Springfield\"," +
                                                                                        "\"codigoPostal\": \"5000\"," +
                                                                                        "\"referencia\": \"Entre pino y roble\"," +
                                                                                        "\"esPrincipal\": false" +
                                                                                        "}"))
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.id").saveAs("direccionId")));

        ScenarioBuilder scnMarcarPrincipal = scenario("Cliente - Crear dirección y marcarla como principal")
                        .exec(
                                        http("POST /auth/login")
                                                        .post("/auth/login")
                                                        .body(StringBody(
                                                                        "{\"email\": \"Alberto@gmail.com\", \"password\": \"Alberto0123\"}"))
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.token").saveAs("jwtToken")))
                        .pause(1)
                        // Crear dirección primero
                        .exec(
                                        http("POST /direcciones (para marcar principal)")
                                                        .post("/direcciones")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .body(StringBody(
                                                                        "{" +
                                                                                        "\"nombre\": \"Trabajo\"," +
                                                                                        "\"calle\": \"Calle Falsa\"," +
                                                                                        "\"numero\": \"123\"," +
                                                                                        "\"localidad\": \"Cordoba\"," +
                                                                                        "\"esPrincipal\": false" +
                                                                                        "}"))
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.id").saveAs("direccionId")))
                        .pause(1)
                        // Marcarla como principal
                        .exec(
                                        http("PUT /direcciones/{id}/principal")
                                                        .put("/direcciones/#{direccionId}/principal")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .check(status().is(200)));

        ScenarioBuilder scnFlujoCRUD = scenario("Cliente - Crear, editar y eliminar dirección")
                        .exec(
                                        http("POST /auth/login")
                                                        .post("/auth/login")
                                                        .body(StringBody(
                                                                        "{\"email\": \"Alberto@gmail.com\", \"password\": \"Alberto0123\"}"))
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.token").saveAs("jwtToken")))
                        .pause(1)
                        // Crear
                        .exec(
                                        http("POST /direcciones (CRUD paso 1)")
                                                        .post("/direcciones")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .body(StringBody(
                                                                        "{" +
                                                                                        "\"nombre\": \"Temporal\"," +
                                                                                        "\"calle\": \"Boulevard Test\"," +
                                                                                        "\"numero\": \"99\"," +
                                                                                        "\"localidad\": \"Rosario\"," +
                                                                                        "\"esPrincipal\": false" +
                                                                                        "}"))
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.id").saveAs("direccionId")))
                        .pause(1)
                        // Editar
                        .exec(
                                        http("PUT /direcciones/{id} (CRUD paso 2)")
                                                        .put("/direcciones/#{direccionId}")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .body(StringBody(
                                                                        "{" +
                                                                                        "\"nombre\": \"Temporal Editada\"," +
                                                                                        "\"calle\": \"Boulevard Test\"," +
                                                                                        "\"numero\": \"100\"," +
                                                                                        "\"localidad\": \"Rosario\"," +
                                                                                        "\"esPrincipal\": false" +
                                                                                        "}"))
                                                        .check(status().is(200)))
                        .pause(1)
                        // Eliminar
                        .exec(
                                        http("DELETE /direcciones/{id} (CRUD paso 3)")
                                                        .delete("/direcciones/#{direccionId}")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .check(status().is(204)));

        {
                setUp(
                        scnListarDirecciones.injectOpen(
                                rampUsers(100).during(30),           // sube a 100 en 30s
                                constantUsersPerSec(10).during(60)   // sostiene ~10 req/s por 1 min más
                        ),
                        scnCrearDireccion.injectOpen(
                                rampUsers(100).during(30),           // sube a 100 en 30s
                                constantUsersPerSec(10).during(60)   // sostiene ~10 req/s por 1 min más
                        ),
                        scnMarcarPrincipal.injectOpen(
                                rampUsers(100).during(30),           // sube a 100 en 30s
                                constantUsersPerSec(10).during(60)   // sostiene ~10 req/s por 1 min más
                        ),
                        scnFlujoCRUD.injectOpen(
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
