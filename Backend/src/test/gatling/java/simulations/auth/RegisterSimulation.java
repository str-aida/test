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

public class RegisterSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // Feeder: genera un email y dni únicos por cada usuario virtual
    Iterator<Map<String, Object>> feeder = Stream.generate((Supplier<Map<String, Object>>) () -> {
        long id = System.nanoTime();
        Map<String, Object> map = new HashMap<>();
        map.put("email", "gatling" + id + "@test.com");
        map.put("dni", String.valueOf(40000000L + (id % 9000000L)));
        return map;
    }).iterator();

    ScenarioBuilder scn = scenario("Registro de clientes")
            .feed(feeder)
            .exec(
                    http("POST /auth/registro-cliente")
                            .post("/auth/registro-cliente")
                            .body(StringBody(
                                    "{\"nombre\": \"Test\", \"apellido\": \"Gatling\", " +
                                    "\"email\": \"#{email}\", \"password\": \"Cliente123*\", " +
                                    "\"telefono\": \"1112345678\", \"dni\": \"#{dni}\", " +
                                    "\"fechaNacimiento\": \"2000-01-01\"}"
                            ))
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