package simulations.pedidos;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import java.util.stream.Stream;
import java.util.function.Supplier;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class PedidosClienteSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    Iterator<Map<String, Object>> feeder = Stream.generate((Supplier<Map<String, Object>>) () -> {
        long id = System.nanoTime();
        Map<String, Object> map = new HashMap<>();
        long[] productosIds = {1L, 2L, 3L, 4L, 5L};
        map.put("productoId", productosIds[(int) (id % productosIds.length)]);
        map.put("cantidad", (int) ((id % 3) + 1));
        return map;
    }).iterator();

    // ── Escenario A: Crear pedido (RETIRO, DOMICILIO, EFECTIVO) ─────────────────
    ScenarioBuilder scnCrearPedido = scenario("Cliente - Crear pedido")
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
                    http("POST /pedidos (crear pedido)")
                            .post("/pedidos")
                            .header("Authorization", "Bearer #{jwtToken}")
                            .body(StringBody(
                                    "{" +
                                    "\"tipoEntrega\": \"RETIRO\"," +
                                    "\"metodoPago\": \"EFECTIVO\"," +
                                    "\"detalles\": [" +
                                    "  {\"productoId\": #{productoId}, \"cantidad\": #{cantidad}}" +
                                    "]" +
                                    "}"
                            ))
                            .check(status().is(201))
                            .check(jsonPath("$.id").saveAs("pedidoId"))
            );

    // ── Escenario B: Listar mis pedidos sin filtros ───────────────────────────
    ScenarioBuilder scnListarPedidos = scenario("Cliente - Listar mis pedidos")
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
                    http("GET /pedidos (listar sin filtros)")
                            .get("/pedidos?page=0&size=20")
                            .header("Authorization", "Bearer #{jwtToken}")
                            .check(status().is(200))
                            .check(jsonPath("$.content").exists())
            );

    // ── Escenario C: Listar pedidos filtrados por estado ─────────────────────
    ScenarioBuilder scnListarPorEstado = scenario("Cliente - Listar pedidos por estado PENDIENTE")
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
                    http("GET /pedidos (estado=PENDIENTE)")
                            .get("/pedidos?estado=PENDIENTE&page=0&size=20")
                            .header("Authorization", "Bearer #{jwtToken}")
                            .check(status().is(200))
            );

    // ── Escenario D: Obtener detalle de un pedido específico ─────────────────
    ScenarioBuilder scnDetallePedido = scenario("Cliente - Ver detalle de pedido")
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
                    // Primero lista para obtener un ID real
                    http("GET /pedidos (obtener id)")
                            .get("/pedidos?page=0&size=1")
                            .header("Authorization", "Bearer #{jwtToken}")
                            .check(status().is(200))
                            .check(jsonPath("$.content[0].id").saveAs("pedidoId"))
            )
            .pause(1)
            .exec(
                    http("GET /pedidos/{id} (detalle)")
                            .get("/pedidos/#{pedidoId}")
                            .header("Authorization", "Bearer #{jwtToken}")
                            .check(status().is(200))
                            .check(jsonPath("$.id").exists())
            );

    {
        setUp(
                scnCrearPedido.injectOpen(
                        rampUsers(100).during(30),           
                        constantUsersPerSec(10).during(60)   
                ),
                scnListarPedidos.injectOpen(
                        rampUsers(100).during(30),           
                        constantUsersPerSec(10).during(60)   
                ),
                scnListarPorEstado.injectOpen(
                        rampUsers(100).during(30),           
                        constantUsersPerSec(10).during(60)   
                ),
                scnDetallePedido.injectOpen(
                        rampUsers(100).during(30),           
                        constantUsersPerSec(10).during(60)   
                )
        ).protocols(httpProtocol)
         .assertions(
                 global().responseTime().max().lt(3000),        // ningún request > 3seg
                 global().successfulRequests().percent().gt(95.0) // al menos 95% éxito
         );
    }
}
