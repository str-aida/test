package com.Trabajo_Final_Beltran.integration.controller;

import com.Trabajo_Final_Beltran.dto.request.CreateDetallePedidoRequest;
import com.Trabajo_Final_Beltran.dto.request.CreatePedidoRequest;
import com.Trabajo_Final_Beltran.entity.*;
import com.Trabajo_Final_Beltran.enums.*;
import com.Trabajo_Final_Beltran.repository.*;
import com.Trabajo_Final_Beltran.security.JwtService;
import com.Trabajo_Final_Beltran.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("PedidoController - Integration Tests")
class PedidoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstablecimientoRepository establecimientoRepository;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private EmailService emailService;

    private Establecimiento establecimiento;
    private Producto producto;
    private String clienteToken;
    private String adminToken;
    private Long pedidoCreadoId;

    @BeforeAll
    void setup() {
        pagoRepository.deleteAll();
        detallePedidoRepository.deleteAll();
        pedidoRepository.deleteAll();
        productoRepository.deleteAll();
        categoriaRepository.deleteAll();

        if (establecimientoRepository.count() == 0) {
            establecimiento = establecimientoRepository.save(Establecimiento.builder()
                    .nombre("Gestia Pedidos Resto")
                    .razonSocial("Gestia Pedidos S.A.")
                    .cuit("30-55667788-9")
                    .email("contacto.pedidos@gestia.com")
                    .telefono("1155667788")
                    .logoUrl("http://localhost/logo.png")
                    .horarioApertura(LocalTime.of(8, 0))
                    .horarioCierre(LocalTime.of(23, 0))
                    .diasHabiles(Set.of(DiaSemana.LUNES, DiaSemana.MARTES, DiaSemana.MIERCOLES,
                            DiaSemana.JUEVES, DiaSemana.VIERNES, DiaSemana.SABADO, DiaSemana.DOMINGO))
                    .tipoServicio(TipoServicio.AMBOS)
                    .estado(EstadoEstablecimiento.ACTIVO)
                    .build());
        } else {
            establecimiento = establecimientoRepository.findAll().get(0);
        }

        Categoria categoria = categoriaRepository.save(Categoria.builder()
                .nombre("Hamburguesas")
                .descripcion("Hamburguesas artesanales")
                .estado(EstadoCategoria.ACTIVO)
                .establecimiento(establecimiento)
                .build());

        producto = productoRepository.save(Producto.builder()
                .nombre("Burger Doble Cheddar")
                .descripcion("Doble carne con abundante cheddar")
                .precio(new BigDecimal("5000.00"))
                .stock(50)
                .codigo("BUR-001")
                .estado(EstadoProducto.ACTIVO)
                .categoria(categoria)
                .establecimiento(establecimiento)
                .build());

        Usuario adminUser = usuarioRepository.findByEmail("admin.pedidos@test.com")
                .orElseGet(() -> usuarioRepository.save(Usuario.builder()
                        .nombre("Admin")
                        .apellido("Pedidos")
                        .email("admin.pedidos@test.com")
                        .password("$2a$10$dummyhashedpassword12345678901234567890")
                        .dni("71001122")
                        .telefono("1177665544")
                        .fechaNacimiento(LocalDate.of(1985, 4, 10))
                        .rol(Rol.ADMIN)
                        .estado(Estado.ACTIVO)
                        .establecimiento(establecimiento)
                        .build()));

        Usuario clienteUser = usuarioRepository.findByEmail("cliente.pedidos@test.com")
                .orElseGet(() -> usuarioRepository.save(Usuario.builder()
                        .nombre("Cliente")
                        .apellido("Pedidos")
                        .email("cliente.pedidos@test.com")
                        .password("$2a$10$dummyhashedpassword12345678901234567890")
                        .dni("72001122")
                        .telefono("1177665545")
                        .fechaNacimiento(LocalDate.of(1994, 7, 20))
                        .rol(Rol.CLIENTE)
                        .estado(Estado.ACTIVO)
                        .establecimiento(establecimiento)
                        .build()));

        adminToken = jwtService.generateToken(adminUser);
        clienteToken = jwtService.generateToken(clienteUser);
    }

    @Test
    @Order(1)
    @DisplayName("1. CLIENTE puede crear un pedido exitosamente (HTTP 201)")
    void clienteDeberiaCrearPedido() throws Exception {
        System.out.println("-> Ejecutando PedidoControllerIT: clienteDeberiaCrearPedido");

        CreatePedidoRequest request = CreatePedidoRequest.builder()
                .tipoEntrega(TipoEntrega.RETIRO)
                .metodoPago(MetodoPago.EFECTIVO)
                .detalles(List.of(
                        CreateDetallePedidoRequest.builder()
                                .productoId(producto.getId())
                                .cantidad(2)
                                .build()
                ))
                .build();

        String response = mockMvc.perform(post("/pedidos")
                        .header("Authorization", "Bearer " + clienteToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andReturn().getResponse().getContentAsString();

        Matcher matcher = Pattern.compile("\"id\":(\\d+)").matcher(response);
        if (matcher.find()) {
            pedidoCreadoId = Long.parseLong(matcher.group(1));
        }

        assertThat(pedidoCreadoId).isNotNull();
        System.out.println("-> OK: Pedido creado con ID " + pedidoCreadoId);
    }

    @Test
    @Order(2)
    @DisplayName("2. CLIENTE puede obtener su pedido por ID (HTTP 200)")
    void clienteDeberiaObtenerPedidoPorId() throws Exception {
        System.out.println("-> Ejecutando PedidoControllerIT: clienteDeberiaObtenerPedidoPorId");

        mockMvc.perform(get("/pedidos/" + pedidoCreadoId)
                        .header("Authorization", "Bearer " + clienteToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pedidoCreadoId))
                .andExpect(jsonPath("$.detalles").isArray());

        System.out.println("-> OK: Pedido obtenido por ID correctamente.");
    }

    @Test
    @Order(3)
    @DisplayName("3. CLIENTE puede listar sus pedidos (HTTP 200)")
    void clienteDeberiaListarSusPedidos() throws Exception {
        System.out.println("-> Ejecutando PedidoControllerIT: clienteDeberiaListarSusPedidos");

        mockMvc.perform(get("/pedidos")
                        .header("Authorization", "Bearer " + clienteToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        System.out.println("-> OK: Pedidos listados por CLIENTE.");
    }

    @Test
    @Order(4)
    @DisplayName("4. ADMIN puede listar pedidos en curso (HTTP 200)")
    void adminDeberiaListarPedidosEnCurso() throws Exception {
        System.out.println("-> Ejecutando PedidoControllerIT: adminDeberiaListarPedidosEnCurso");

        mockMvc.perform(get("/pedidos/en-curso")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        System.out.println("-> OK: Pedidos en curso listados por ADMIN.");
    }

    @Test
    @Order(5)
    @DisplayName("5. ADMIN puede aceptar un pedido (HTTP 200)")
    void adminDeberiaAceptarPedido() throws Exception {
        System.out.println("-> Ejecutando PedidoControllerIT: adminDeberiaAceptarPedido");

        mockMvc.perform(put("/pedidos/" + pedidoCreadoId + "/aceptar")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACEPTADO"));

        System.out.println("-> OK: Pedido aceptado correctamente.");
    }

    @Test
    @Order(6)
    @DisplayName("6. ADMIN puede pasar pedido a en preparación (HTTP 200)")
    void adminDeberiaPasarPedidoAEnPreparacion() throws Exception {
        System.out.println("-> Ejecutando PedidoControllerIT: adminDeberiaPasarPedidoAEnPreparacion");

        mockMvc.perform(put("/pedidos/" + pedidoCreadoId + "/en-preparacion")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_PREPARACION"));

        System.out.println("-> OK: Pedido pasado a en preparacion correctamente.");
    }

    @Test
    @Order(7)
    @DisplayName("7. ADMIN puede marcar pedido como listo (HTTP 200)")
    void adminDeberiaMarcarPedidoComoListo() throws Exception {
        System.out.println("-> Ejecutando PedidoControllerIT: adminDeberiaMarcarPedidoComoListo");

        mockMvc.perform(put("/pedidos/" + pedidoCreadoId + "/listo")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("LISTO"));

        System.out.println("-> OK: Pedido marcado como listo correctamente.");
    }

    @Test
    @Order(8)
    @DisplayName("8. ADMIN puede marcar pedido como entregado (HTTP 200)")
    void adminDeberiaMarcarPedidoComoEntregado() throws Exception {
        System.out.println("-> Ejecutando PedidoControllerIT: adminDeberiaMarcarPedidoComoEntregado");

        Pedido p = pedidoRepository.findById(pedidoCreadoId).orElseThrow();
        pagoRepository.save(Pago.builder()
                .pedido(p)
                .monto(p.getTotal())
                .metodo(MetodoPago.EFECTIVO)
                .estado(EstadoPago.PENDIENTE)
                .fechaCreacion(java.time.LocalDateTime.now())
                .version(0L)
                .build());

        mockMvc.perform(put("/pedidos/" + pedidoCreadoId + "/entregado")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENTREGADO"));

        System.out.println("-> OK: Pedido marcado como entregado correctamente.");
    }

    @Test
    @Order(9)
    @DisplayName("9. CLIENTE no puede cambiar estado de pedido a aceptar (HTTP 403)")
    void clienteNoDeberiaAceptarPedido() throws Exception {
        System.out.println("-> Ejecutando PedidoControllerIT: clienteNoDeberiaAceptarPedido");

        mockMvc.perform(put("/pedidos/" + pedidoCreadoId + "/aceptar")
                        .header("Authorization", "Bearer " + clienteToken))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Aceptar pedido rechazado con HTTP 403 para rol CLIENTE.");
    }

    @Test
    @Order(10)
    @DisplayName("10. Petición sin token debe fallar con HTTP 403")
    void sinTokenDeberiaFallar() throws Exception {
        System.out.println("-> Ejecutando PedidoControllerIT: sinTokenDeberiaFallar");

        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isForbidden());

        System.out.println("-> OK: Peticion sin token rechazada con HTTP 403.");
    }
}
