package com.Trabajo_Final_Beltran.integration.controller;

import com.Trabajo_Final_Beltran.dto.request.CreateProductoRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdateProductoRequest;
import com.Trabajo_Final_Beltran.entity.Categoria;
import com.Trabajo_Final_Beltran.entity.Establecimiento;
import com.Trabajo_Final_Beltran.entity.Producto;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.*;
import com.Trabajo_Final_Beltran.repository.CategoriaRepository;
import com.Trabajo_Final_Beltran.repository.DescuentoRepository;
import com.Trabajo_Final_Beltran.repository.EstablecimientoRepository;
import com.Trabajo_Final_Beltran.repository.ProductoRepository;
import com.Trabajo_Final_Beltran.repository.UsuarioRepository;
import com.Trabajo_Final_Beltran.security.JwtService;
import com.Trabajo_Final_Beltran.service.EmailService;
import com.Trabajo_Final_Beltran.service.ImageStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("ProductoController - Integration Tests")
class ProductoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private DescuentoRepository descuentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstablecimientoRepository establecimientoRepository;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private ImageStorageService imageStorageService;

    private Establecimiento establecimiento;
    private Categoria categoria;
    private Usuario adminUsuario;
    private String adminToken;

    @BeforeEach
    void setUp() {
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
                    .diasHabiles(Set.of(DiaSemana.LUNES, DiaSemana.MARTES, DiaSemana.MIERCOLES, DiaSemana.JUEVES, DiaSemana.VIERNES))
                    .tipoServicio(TipoServicio.AMBOS)
                    .estado(EstadoEstablecimiento.ACTIVO)
                    .build());
        } else {
            establecimiento = establecimientoRepository.findAll().get(0);
        }

        categoria = categoriaRepository.save(Categoria.builder()
                .nombre("Pizzas")
                .descripcion("Pizzas a la piedra")
                .estado(EstadoCategoria.ACTIVO)
                .establecimiento(establecimiento)
                .build());

        adminUsuario = usuarioRepository.findByEmail("admin.producto@test.com").orElseGet(() ->
                usuarioRepository.save(Usuario.builder()
                        .nombre("Admin")
                        .apellido("Producto")
                        .email("admin.producto@test.com")
                        .password("$2a$10$dummyhashedpassword12345678901234567890")
                        .dni("33445566")
                        .telefono("1155667788")
                        .fechaNacimiento(LocalDate.of(1991, 2, 2))
                        .rol(Rol.ADMIN)
                        .estado(Estado.ACTIVO)
                        .establecimiento(establecimiento)
                        .build())
        );

        adminToken = jwtService.generateToken(adminUsuario);

        when(imageStorageService.guardar(any(), any())).thenReturn("https://cloudinary.com/imagen-demo.png");
    }

    @Test
    @DisplayName("Flujo completo: Crear producto con multipart exitosamente")
    void testCrearProductoExitoso() throws Exception {
        System.out.println("-> Ejecutando ProductoControllerIT: testCrearProductoExitoso");
        CreateProductoRequest request = CreateProductoRequest.builder()
                .nombre("Pizza Muzzarella")
                .descripcion("Salsa de tomate, muzzarella y oregano")
                .precio(new BigDecimal("9500.00"))
                .categoriaId(categoria.getId())
                .stock(20)
                .codigo("PIZ-MUZ-01")
                .build();

        MockMultipartFile productoPart = new MockMultipartFile(
                "producto",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile imagenPart = new MockMultipartFile(
                "imagen",
                "pizza.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake image content".getBytes()
        );

        mockMvc.perform(multipart("/productos")
                        .file(productoPart)
                        .file(imagenPart)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Producto creado correctamente"));

        List<Producto> productos = productoRepository.findAll();
        assertThat(productos).hasSize(1);
        assertThat(productos.get(0).getNombre()).isEqualTo("Pizza Muzzarella");
        assertThat(productos.get(0).getCodigo()).isEqualTo("PIZ-MUZ-01");
        System.out.println("-> OK: Producto creado con multipart y verificado en base de datos correctamente.");
    }

    @Test
    @DisplayName("Listar productos con filtros y token valido")
    void testListarProductos() throws Exception {
        System.out.println("-> Ejecutando ProductoControllerIT: testListarProductos");
        productoRepository.save(Producto.builder()
                .nombre("Pizza Especial")
                .descripcion("Jamon y morrones")
                .precio(new BigDecimal("11000.00"))
                .stock(15)
                .codigo("PIZ-ESP-02")
                .estado(EstadoProducto.ACTIVO)
                .categoria(categoria)
                .establecimiento(establecimiento)
                .build());

        mockMvc.perform(get("/productos")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("categoriaId", categoria.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nombre").value("Pizza Especial"))
                .andExpect(jsonPath("$[0].precio").value(11000.00));
        System.out.println("-> OK: Listado de productos obtenido con exito.");
    }

    @Test
    @DisplayName("Editar producto existente mediante multipart")
    void testEditarProducto() throws Exception {
        System.out.println("-> Ejecutando ProductoControllerIT: testEditarProducto");
        Producto producto = productoRepository.save(Producto.builder()
                .nombre("Pizza Fugazzeta")
                .descripcion("Cebolla y queso")
                .precio(new BigDecimal("10500.00"))
                .stock(10)
                .codigo("PIZ-FUG-03")
                .estado(EstadoProducto.ACTIVO)
                .categoria(categoria)
                .establecimiento(establecimiento)
                .build());

        UpdateProductoRequest updateReq = UpdateProductoRequest.builder()
                .nombre("Pizza Fugazzeta Rellena")
                .descripcion("Doble queso y cebolla")
                .precio(new BigDecimal("12500.00"))
                .categoriaId(categoria.getId())
                .estado(EstadoProducto.ACTIVO)
                .stock(12)
                .codigo("PIZ-FUG-03")
                .build();

        MockMultipartFile productoPart = new MockMultipartFile(
                "producto",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(updateReq)
        );

        mockMvc.perform(multipart(HttpMethod.PUT, "/productos/" + producto.getId())
                        .file(productoPart)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Pizza Fugazzeta Rellena"))
                .andExpect(jsonPath("$.precio").value(12500.00));
        System.out.println("-> OK: Producto editado y validado correctamente.");
    }

    @Test
    @DisplayName("Crear producto falla con 400 si faltan campos requeridos")
    void testCrearProductoFallaValidacion() throws Exception {
        System.out.println("-> Ejecutando ProductoControllerIT: testCrearProductoFallaValidacion");
        CreateProductoRequest invalidRequest = CreateProductoRequest.builder()
                .nombre("") // Blanco
                .precio(null) // Nulo
                .build();

        MockMultipartFile productoPart = new MockMultipartFile(
                "producto",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(invalidRequest)
        );

        mockMvc.perform(multipart("/productos")
                        .file(productoPart)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest());
        System.out.println("-> OK: Validacion 400 Bad Request recibida correctamente.");
    }

    @Test
    @DisplayName("Peticiones sin autenticacion son rechazadas con 403 Forbidden")
    void testAccesoSinAutenticacion() throws Exception {
        System.out.println("-> Ejecutando ProductoControllerIT: testAccesoSinAutenticacion");
        mockMvc.perform(get("/productos"))
                .andExpect(status().isForbidden());
        System.out.println("-> OK: Acceso no autenticado rechazado con HTTP 403 Forbidden.");
    }
}
