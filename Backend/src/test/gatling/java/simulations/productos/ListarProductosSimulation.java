package simulations.productos;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import java.util.stream.Stream;
import java.util.function.Supplier;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;


public class ListarProductosSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // Feeder: varía los parámetros de búsqueda para simular distintos patrones de uso
    Iterator<Map<String, Object>> feeder = Stream.generate((Supplier<Map<String, Object>>) () -> {
        long id = System.nanoTime();
        Map<String, Object> map = new HashMap<>();
        // Rota entre categorías 1 a 5
        map.put("categoriaId", (id % 5) + 1);
        // Simula búsquedas por texto variadas
        String[] textos = {"pizza", "pasta", "burger", "pollo", "cafe"};
        map.put("texto", textos[(int) (id % textos.length)]);
        return map;
    }).iterator();

    // ── Escenario A: Listar todos los productos sin filtros ───────────────────
    ScenarioBuilder scnSinFiltros = scenario("Cliente - Listar todos los productos")
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
            .exec(
                    http("GET /productos (sin filtros)")
                            .get("/productos")
                            .header("Authorization", "Bearer #{jwtToken}")
                            .check(status().is(200))
                            .check(jsonPath("$[*]").exists())
            );

    // ── Escenario B: Filtrar por categoría ───────────────────────────────────
    ScenarioBuilder scnPorCategoria = scenario("Cliente - Productos por categoría")
            .feed(feeder)
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
            .exec(
                    http("GET /productos (por categoriaId)")
                            .get("/productos?categoriaId=#{categoriaId}")
                            .header("Authorization", "Bearer #{jwtToken}")
                            .check(status().is(200))
            );

    // ── Escenario C: Búsqueda por texto ──────────────────────────────────────
    ScenarioBuilder scnPorTexto = scenario("Cliente - Búsqueda de productos por texto")
            .feed(feeder)
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
            .exec(
                    http("GET /productos (búsqueda por texto)")
                            .get("/productos?texto=#{texto}")
                            .header("Authorization", "Bearer #{jwtToken}")
                            .check(status().is(200))
            );

    // ── Escenario D: Filtrar por estado ACTIVO ────────────────────────────────
    ScenarioBuilder scnPorEstado = scenario("Cliente - Productos activos")
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
            .exec(
                    http("GET /productos (estado=ACTIVO)")
                            .get("/productos?estado=ACTIVO")
                            .header("Authorization", "Bearer #{jwtToken}")
                            .check(status().is(200))
            );

    {
        setUp(
                scnSinFiltros.injectOpen(rampUsers(100).during(10)),
                scnPorCategoria.injectOpen(rampUsers(100).during(10)),
                scnPorTexto.injectOpen(rampUsers(50).during(10)),
                scnPorEstado.injectOpen(rampUsers(50).during(10))
        ).protocols(httpProtocol);
    }
}
