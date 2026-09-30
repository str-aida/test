package simulations.auth;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import java.util.stream.Stream;
import java.util.function.Supplier;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;


public class RestablecerPasswordSimulation extends Simulation {

    private static final String TOKEN_RECUPERACION = "REEMPLAZAR_CON_TOKEN_REAL";

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // ── Escenario A: token válido (restablecimiento exitoso) ──────────────────
    ScenarioBuilder scnValido = scenario("Restablecer password - token válido")
            .exec(
                    http("POST /auth/restablecer-password (token válido)")
                            .post("/auth/restablecer-password")
                            .body(StringBody(
                                    "{\"token\": \"" + TOKEN_RECUPERACION + "\", " +
                                    "\"nuevaPassword\": \"NuevaClave1\"}"
                            ))
                            .check(status().is(200))
            );

    // ── Escenario B: token inválido (validación de error) ────────────────────
    // Feeder: genera tokens aleatorios para simular tokens inválidos/expirados
    Iterator<Map<String, Object>> feederInvalido = Stream.generate((Supplier<Map<String, Object>>) () -> {
        long id = System.nanoTime();
        Map<String, Object> map = new HashMap<>();
        map.put("tokenInvalido", "token-invalido-" + id);
        return map;
    }).iterator();

    ScenarioBuilder scnInvalido = scenario("Restablecer password - token inválido")
            .feed(feederInvalido)
            .exec(
                    http("POST /auth/restablecer-password (token inválido)")
                            .post("/auth/restablecer-password")
                            .body(StringBody(
                                    "{\"token\": \"#{tokenInvalido}\", " +
                                    "\"nuevaPassword\": \"NuevaClave1\"}"
                            ))
                            // El backend debe rechazar tokens inválidos con 400 o 404
                            .check(status().in(400, 404))
            );

    {
        setUp(
                scnValido.injectOpen(
                        rampUsers(100).during(30),           // sube a 100 en 30s
                        constantUsersPerSec(10).during(60)   // sostiene ~10 req/s por 1 min más
                ),
                scnInvalido.injectOpen(
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
