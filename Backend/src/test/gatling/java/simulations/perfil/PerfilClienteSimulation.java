package simulations.perfil;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class PerfilClienteSimulation extends Simulation {

        HttpProtocolBuilder httpProtocol = http
                        .baseUrl("http://localhost:8080")
                        .acceptHeader("application/json")
                        .contentTypeHeader("application/json");

        ScenarioBuilder scnObtenerPerfil = scenario("Cliente - Obtener mi perfil")
                        .exec(
                                        http("POST /auth/login")
                                                        .post("/auth/login")
                                                        .body(StringBody(
                                                                        "{\"email\": \"Alberto@gmail.com\", \"password\": \"Alberto0123\"}"))
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.token").saveAs("jwtToken")))
                        .pause(1)
                        .exec(
                                        http("GET /perfil")
                                                        .get("/perfil")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.email").exists()));

        ScenarioBuilder scnActualizarPerfil = scenario("Cliente - Actualizar mi perfil")
                        .exec(
                                        http("POST /auth/login")
                                                        .post("/auth/login")
                                                        .body(StringBody(
                                                                        "{\"email\": \"Alberto@gmail.com\", \"password\": \"Alberto0123\"}"))
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.token").saveAs("jwtToken")))
                        .pause(1)
                        .exec(
                                        http("PUT /perfil")
                                                        .put("/perfil")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .body(StringBody(
                                                                        "{" +
                                                                                        "\"nombre\": \"Alberto\"," +
                                                                                        "\"apellido\": \"GarciaTest\"," +
                                                                                        "\"telefono\": \"1122334455\"," +
                                                                                        "\"fechaNacimiento\": \"1990-05-15\"" +
                                                                                        "}"))
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.nombre").exists()));

        ScenarioBuilder scnSolicitarCambioPassword = scenario("Cliente - Solicitar cambio de contraseña")
                        .exec(
                                        http("POST /auth/login")
                                                        .post("/auth/login")
                                                        .body(StringBody(
                                                                        "{\"email\": \"Alberto@gmail.com\", \"password\": \"Alberto0123\"}"))
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.token").saveAs("jwtToken")))
                        .pause(1)
                        // Solicita el envío de email para cambiar contraseña
                        .exec(
                                        http("POST /perfil/password (solicitar cambio)")
                                                        .post("/perfil/password")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        // Acepta 200 (email enviado) o 500 si el servicio SMTP no está disponible en test
                                                        .check(status().in(200, 500)));

        ScenarioBuilder scnFlujoPerfil = scenario("Cliente - Leer y actualizar perfil en secuencia")
                        .exec(
                                        http("POST /auth/login")
                                                        .post("/auth/login")
                                                        .body(StringBody(
                                                                        "{\"email\": \"Alberto@gmail.com\", \"password\": \"Alberto0123\"}"))
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.token").saveAs("jwtToken")))
                        .pause(1)
                        .exec(
                                        http("GET /perfil (leer datos actuales)")
                                                        .get("/perfil")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .check(status().is(200))
                                                        .check(jsonPath("$.nombre").saveAs("nombreActual")))
                        .pause(1)
                        .exec(
                                        http("PUT /perfil (actualizar)")
                                                        .put("/perfil")
                                                        .header("Authorization", "Bearer #{jwtToken}")
                                                        .body(StringBody(
                                                                        "{" +
                                                                                        "\"nombre\": \"#{nombreActual}\"," +
                                                                                        "\"apellido\": \"ApellidoActualizado\"," +
                                                                                        "\"telefono\": \"1199887766\"," +
                                                                                        "\"fechaNacimiento\": \"1990-05-15\"" +
                                                                                        "}"))
                                                        .check(status().is(200)));

        {
                setUp(
                        scnObtenerPerfil.injectOpen(
                                rampUsers(100).during(30),           // sube a 100 en 30s
                                constantUsersPerSec(10).during(60)   // sostiene ~10 req/s por 1 min más
                        ),
                        scnActualizarPerfil.injectOpen(
                                rampUsers(100).during(30),           // sube a 100 en 30s
                                constantUsersPerSec(10).during(60)   // sostiene ~10 req/s por 1 min más
                        ),
                        scnSolicitarCambioPassword.injectOpen(
                                rampUsers(100).during(30),           // sube a 100 en 30s
                                constantUsersPerSec(10).during(60)   // sostiene ~10 req/s por 1 min más
                        ),
                        scnFlujoPerfil.injectOpen(
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
