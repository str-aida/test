package com.Trabajo_Final_Beltran.integration.controller;

import com.Trabajo_Final_Beltran.dto.request.CreateCategoriaRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdateCategoriaRequest;
import com.Trabajo_Final_Beltran.entity.Categoria;
import com.Trabajo_Final_Beltran.entity.Establecimiento;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.*;
import com.Trabajo_Final_Beltran.repository.CategoriaRepository;
import com.Trabajo_Final_Beltran.repository.EstablecimientoRepository;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("CategoriaController - Integration Tests")
class CategoriaControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

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
    private Usuario adminUsuario;
    private String adminToken;

    @BeforeEach
    void setUp() {
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

        adminUsuario = usuarioRepository.findByEmail("admin.categoria@test.com").orElseGet(() ->
                usuarioRepository.save(Usuario.builder()
                        .nombre("Admin")
                        .apellido("Categoria")
                        .email("admin.categoria@test.com")
                        .password("$2a$10$dummyhashedpassword12345678901234567890")
                        .dni("22334455")
                        .telefono("1144556677")
                        .fechaNacimiento(LocalDate.of(1990, 1, 1))
                        .rol(Rol.ADMIN)
                        .estado(Estado.ACTIVO)
                        .establecimiento(establecimiento)
                        .build())
        );

        adminToken = jwtService.generateToken(adminUsuario);
    }

    @Test
    @DisplayName("Flujo completo: Crear categoria exitosamente como ADMIN")
    void testCrearCategoriaExitoso() throws Exception {
        System.out.println("-> Ejecutando CategoriaControllerIT: testCrearCategoriaExitoso");
        CreateCategoriaRequest request = CreateCategoriaRequest.builder()
                .nombre("Hamburguesas")
                .descripcion("Hamburguesas artesanales y clasicas")
                .build();

        mockMvc.perform(post("/categorias")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Categoría creada correctamente"));

        List<Categoria> categorias = categoriaRepository.findAll();
        assertThat(categorias).hasSize(1);
        assertThat(categorias.get(0).getNombre()).isEqualTo("Hamburguesas");
        assertThat(categorias.get(0).getEstado()).isEqualTo(EstadoCategoria.ACTIVO);
        System.out.println("-> OK: Categoria creada y verificada en base de datos correctamente.");
    }

    @Test
    @DisplayName("Listar categorias del establecimiento con token valido")
    void testListarCategorias() throws Exception {
        System.out.println("-> Ejecutando CategoriaControllerIT: testListarCategorias");
        categoriaRepository.save(Categoria.builder()
                .nombre("Bebidas")
                .descripcion("Bebidas con y sin alcohol")
                .estado(EstadoCategoria.ACTIVO)
                .establecimiento(establecimiento)
                .build());

        mockMvc.perform(get("/categorias")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nombre").value("Bebidas"));
        System.out.println("-> OK: Listado de categorias obtenido correctamente.");
    }

    @Test
    @DisplayName("Editar categoria existente exitosamente")
    void testEditarCategoria() throws Exception {
        System.out.println("-> Ejecutando CategoriaControllerIT: testEditarCategoria");
        Categoria categoria = categoriaRepository.save(Categoria.builder()
                .nombre("Postres")
                .descripcion("Postres caseros")
                .estado(EstadoCategoria.ACTIVO)
                .establecimiento(establecimiento)
                .build());

        UpdateCategoriaRequest updateReq = UpdateCategoriaRequest.builder()
                .nombre("Postres gourmet")
                .descripcion("Postres gourmet y helados")
                .estado(EstadoCategoria.ACTIVO)
                .build();

        mockMvc.perform(put("/categorias/" + categoria.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Postres gourmet"))
                .andExpect(jsonPath("$.descripcion").value("Postres gourmet y helados"));
        System.out.println("-> OK: Categoria editada y validada correctamente.");
    }

    @Test
    @DisplayName("Eliminar (desactivar) categoria exitosamente")
    void testEliminarCategoria() throws Exception {
        System.out.println("-> Ejecutando CategoriaControllerIT: testEliminarCategoria");
        Categoria categoria = categoriaRepository.save(Categoria.builder()
                .nombre("Entradas")
                .descripcion("Entradas frias y calientes")
                .estado(EstadoCategoria.ACTIVO)
                .establecimiento(establecimiento)
                .build());

        mockMvc.perform(delete("/categorias/" + categoria.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        Categoria categoriaDesactivada = categoriaRepository.findById(categoria.getId()).orElseThrow();
        assertThat(categoriaDesactivada.getEstado()).isEqualTo(EstadoCategoria.INACTIVO);
        System.out.println("-> OK: Categoria desactivada correctamente con HTTP 204.");
    }

    @Test
    @DisplayName("Crear categoria falla con 400 si nombre esta en blanco")
    void testCrearCategoriaFallaValidacion() throws Exception {
        System.out.println("-> Ejecutando CategoriaControllerIT: testCrearCategoriaFallaValidacion");
        CreateCategoriaRequest requestInvalido = CreateCategoriaRequest.builder()
                .nombre("")
                .descripcion("Sin nombre")
                .build();

        mockMvc.perform(post("/categorias")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());
        System.out.println("-> OK: Validacion 400 Bad Request recibida correctamente para nombre invalido.");
    }

    @Test
    @DisplayName("Peticiones sin autenticacion son rechazadas con 403 Forbidden")
    void testAccesoSinAutenticacion() throws Exception {
        System.out.println("-> Ejecutando CategoriaControllerIT: testAccesoSinAutenticacion");
        mockMvc.perform(get("/categorias"))
                .andExpect(status().isForbidden());
        System.out.println("-> OK: Acceso no autorizado rechazado correctamente.");
    }
}
