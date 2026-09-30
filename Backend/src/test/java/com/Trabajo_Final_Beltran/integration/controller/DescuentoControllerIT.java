package com.Trabajo_Final_Beltran.integration.controller;

import com.Trabajo_Final_Beltran.dto.request.CreateDescuentoRequest;
import com.Trabajo_Final_Beltran.dto.request.ProductoDescuentoRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdateDescuentoRequest;
import com.Trabajo_Final_Beltran.entity.Categoria;
import com.Trabajo_Final_Beltran.entity.Descuento;
import com.Trabajo_Final_Beltran.entity.Establecimiento;
import com.Trabajo_Final_Beltran.entity.Producto;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.*;
import com.Trabajo_Final_Beltran.repository.CategoriaRepository;
import com.Trabajo_Final_Beltran.repository.DescuentoProductoRepository;
import com.Trabajo_Final_Beltran.repository.DescuentoRepository;
import com.Trabajo_Final_Beltran.repository.EstablecimientoRepository;
import com.Trabajo_Final_Beltran.repository.ProductoRepository;
import com.Trabajo_Final_Beltran.repository.UsuarioRepository;
import com.Trabajo_Final_Beltran.security.JwtService;
import com.Trabajo_Final_Beltran.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("DescuentoController - Integration Tests")
class DescuentoControllerIT {

        @Autowired
        private MockMvc mockMvc;

        private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

        @Autowired
        private DescuentoRepository descuentoRepository;

        @Autowired
        private DescuentoProductoRepository descuentoProductoRepository;

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
        private Categoria categoria;
        private Producto producto;
        private Usuario adminUsuario;
        private String adminToken;

        @BeforeEach
        void setUp() {
                descuentoProductoRepository.deleteAll();
                descuentoRepository.deleteAll();
                productoRepository.deleteAll();
                categoriaRepository.deleteAll();

                if (establecimientoRepository.count() == 0) {
                        establecimiento = establecimientoRepository.save(Establecimiento.builder()
                                        .nombre("Gestia Restaurant")
                                        .razonSocial("Gestia S.A.")
                                        .cuit("30-12345678-9")
                                        .email("contacto@gestia.com")
                                        .telefono("1122334455")
                                        .logoUrl("http://localhost/logo.png")
                                        .horarioApertura(LocalTime.of(8, 0))
                                        .horarioCierre(LocalTime.of(23, 0))
                                        .diasHabiles(Set.of(DiaSemana.LUNES, DiaSemana.MARTES, DiaSemana.MIERCOLES,
                                                        DiaSemana.JUEVES, DiaSemana.VIERNES))
                                        .tipoServicio(TipoServicio.AMBOS)
                                        .estado(EstadoEstablecimiento.ACTIVO)
                                        .build());
                } else {
                        establecimiento = establecimientoRepository.findAll().get(0);
                }

                categoria = categoriaRepository.save(Categoria.builder()
                                .nombre("Bebidas")
                                .descripcion("Bebidas frias")
                                .estado(EstadoCategoria.ACTIVO)
                                .establecimiento(establecimiento)
                                .build());

                producto = productoRepository.save(Producto.builder()
                                .nombre("Gaseosa Cola 500ml")
                                .descripcion("Bebida cola")
                                .precio(new BigDecimal("2000.00"))
                                .stock(50)
                                .codigo("GAS-COL-01")
                                .estado(EstadoProducto.ACTIVO)
                                .categoria(categoria)
                                .establecimiento(establecimiento)
                                .build());

                adminUsuario = usuarioRepository.findByEmail("admin.descuento@test.com")
                                .orElseGet(() -> usuarioRepository.save(Usuario.builder()
                                                .nombre("Admin")
                                                .apellido("Descuento")
                                                .email("admin.descuento@test.com")
                                                .password("$2a$10$dummyhashedpassword12345678901234567890")
                                                .dni("44556677")
                                                .telefono("1166778899")
                                                .fechaNacimiento(LocalDate.of(1989, 3, 3))
                                                .rol(Rol.ADMIN)
                                                .estado(Estado.ACTIVO)
                                                .establecimiento(establecimiento)
                                                .build()));

                adminToken = jwtService.generateToken(adminUsuario);
        }

        @Test
        @DisplayName("Flujo completo: Crear descuento exitosamente como ADMIN")
        void testCrearDescuentoExitoso() throws Exception {
                System.out.println("-> Ejecutando DescuentoControllerIT: testCrearDescuentoExitoso");
                CreateDescuentoRequest request = CreateDescuentoRequest.builder()
                                .nombre("Promo Happy Hour")
                                .tipo(TipoCampanaDescuento.GENERAL)
                                .fechaInicio(LocalDate.now())
                                .fechaFin(LocalDate.now().plusDays(7))
                                .productos(List.of(
                                                ProductoDescuentoRequest.builder()
                                                                .productoId(producto.getId())
                                                                .porcentaje(new BigDecimal("15.00"))
                                                                .build()))
                                .build();

                mockMvc.perform(post("/descuentos")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").exists())
                                .andExpect(jsonPath("$.nombre").value("Promo Happy Hour"))
                                .andExpect(jsonPath("$.productos").isArray())
                                .andExpect(jsonPath("$.productos[0].porcentaje").value(15.0));

                List<Descuento> descuentos = descuentoRepository.findAll();
                assertThat(descuentos).hasSize(1);
                assertThat(descuentos.get(0).getNombre()).isEqualTo("Promo Happy Hour");
                System.out.println("-> OK: Descuento creado y verificado en base de datos correctamente.");
        }

        @Test
        @DisplayName("Listar descuentos del establecimiento")
        void testListarDescuentos() throws Exception {
                System.out.println("-> Ejecutando DescuentoControllerIT: testListarDescuentos");
                Descuento descuento = Descuento.builder()
                                .nombre("Descuento Verano")
                                .tipo(TipoCampanaDescuento.GENERAL)
                                .fechaInicio(LocalDate.now())
                                .fechaFin(LocalDate.now().plusDays(10))
                                .estado(EstadoDescuento.ACTIVO)
                                .establecimiento(establecimiento)
                                .productos(new ArrayList<>())
                                .build();
                descuentoRepository.save(descuento);

                mockMvc.perform(get("/descuentos")
                                .header("Authorization", "Bearer " + adminToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[0].nombre").value("Descuento Verano"));
                System.out.println("-> OK: Listado de descuentos obtenido correctamente.");
        }

        @Test
        @DisplayName("Obtener descuento por ID")
        void testObtenerDescuentoPorId() throws Exception {
                System.out.println("-> Ejecutando DescuentoControllerIT: testObtenerDescuentoPorId");
                Descuento descuento = Descuento.builder()
                                .nombre("Descuento Invierno")
                                .tipo(TipoCampanaDescuento.GENERAL)
                                .fechaInicio(LocalDate.now())
                                .fechaFin(LocalDate.now().plusDays(5))
                                .estado(EstadoDescuento.ACTIVO)
                                .establecimiento(establecimiento)
                                .productos(new ArrayList<>())
                                .build();
                Descuento guardado = descuentoRepository.save(descuento);

                mockMvc.perform(get("/descuentos/" + guardado.getId())
                                .header("Authorization", "Bearer " + adminToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(guardado.getId()))
                                .andExpect(jsonPath("$.nombre").value("Descuento Invierno"));
                System.out.println("-> OK: Descuento por ID obtenido correctamente.");
        }

        @Test
        @DisplayName("Editar descuento existente")
        void testEditarDescuento() throws Exception {
                System.out.println("-> Ejecutando DescuentoControllerIT: testEditarDescuento");
                Descuento descuento = Descuento.builder()
                                .nombre("Promo Fin de Semana")
                                .tipo(TipoCampanaDescuento.GENERAL)
                                .fechaInicio(LocalDate.now())
                                .fechaFin(LocalDate.now().plusDays(3))
                                .estado(EstadoDescuento.ACTIVO)
                                .establecimiento(establecimiento)
                                .productos(new ArrayList<>())
                                .build();
                Descuento guardado = descuentoRepository.save(descuento);

                UpdateDescuentoRequest updateReq = UpdateDescuentoRequest.builder()
                                .nombre("Promo Fin de Semana Actualizada")
                                .tipo(TipoCampanaDescuento.GENERAL)
                                .fechaInicio(LocalDate.now())
                                .fechaFin(LocalDate.now().plusDays(4))
                                .productos(List.of(
                                                ProductoDescuentoRequest.builder()
                                                                .productoId(producto.getId())
                                                                .porcentaje(new BigDecimal("20.00"))
                                                                .build()))
                                .build();

                mockMvc.perform(put("/descuentos/" + guardado.getId())
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateReq)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nombre").value("Promo Fin de Semana Actualizada"))
                                .andExpect(jsonPath("$.productos[0].porcentaje").value(20.0));
                System.out.println("-> OK: Descuento editado y validado correctamente.");
        }

        @Test
        @DisplayName("Desactivar y activar descuento (PATCH)")
        void testActivarYDesactivarDescuento() throws Exception {
                System.out.println("-> Ejecutando DescuentoControllerIT: testActivarYDesactivarDescuento");
                Descuento descuento = Descuento.builder()
                                .nombre("Descuento Temporal")
                                .tipo(TipoCampanaDescuento.GENERAL)
                                .fechaInicio(LocalDate.now())
                                .fechaFin(LocalDate.now().plusDays(5))
                                .estado(EstadoDescuento.ACTIVO)
                                .establecimiento(establecimiento)
                                .productos(new ArrayList<>())
                                .build();
                Descuento guardado = descuentoRepository.save(descuento);

                // Desactivar
                mockMvc.perform(patch("/descuentos/" + guardado.getId() + "/desactivar")
                                .header("Authorization", "Bearer " + adminToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.mensaje").value("Descuento desactivado correctamente"));

                // Activar
                mockMvc.perform(patch("/descuentos/" + guardado.getId() + "/activar")
                                .header("Authorization", "Bearer " + adminToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.mensaje").value("Descuento activado correctamente"));
                System.out.println("-> OK: Descuento desactivado y reactivado correctamente via PATCH.");
        }

        @Test
        @DisplayName("Crear descuento falla con 400 si faltan campos obligatorios")
        void testCrearDescuentoFallaValidacion() throws Exception {
                System.out.println("-> Ejecutando DescuentoControllerIT: testCrearDescuentoFallaValidacion");
                CreateDescuentoRequest invalidRequest = CreateDescuentoRequest.builder()
                                .nombre("") // Blanco
                                .productos(List.of()) // Vacio
                                .build();

                mockMvc.perform(post("/descuentos")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
                System.out.println("-> OK: Validacion 400 Bad Request recibida correctamente.");
        }

        @Test
        @DisplayName("Peticiones sin autenticacion son rechazadas con 403 Forbidden")
        void testAccesoSinAutenticacion() throws Exception {
                System.out.println("-> Ejecutando DescuentoControllerIT: testAccesoSinAutenticacion");
                mockMvc.perform(get("/descuentos"))
                                .andExpect(status().isForbidden());
                System.out.println("-> OK: Acceso no autenticado rechazado con HTTP 403 Forbidden.");
        }
}
