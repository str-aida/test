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


public class SolicitarRecuperacionSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // Feeder: varía el email para simular distintos usuarios solicitando recuperación
    Iterator<Map<String, Object>> feeder = Stream.generate((Supplier<Map<String, Object>>) () -> {
        long id = System.nanoTime();
        Map<String, Object> map = new HashMap<>();
        // Alterna entre emails registrados y no registrados para probar ambos casos
        map.put("email", "gatling" + (id % 100) + "@test.com");
        return map;
    }).iterator();

    ScenarioBuilder scn = scenario("Solicitar recuperación de contraseña")
            .feed(feeder)
            .exec(
                    http("POST /auth/solicitar-recuperacion")
                            .post("/auth/solicitar-recuperacion")
                            .body(StringBody(
                                    "{\"email\": \"#{email}\"}"
                            ))
                            // El endpoint devuelve 200 independientemente de si el email existe
                            .check(status().is(200))
            );

    {
        setUp(
                scn.injectOpen(
                        rampUsers(300).during(10)
                )
        ).protocols(httpProtocol);
    }
}
