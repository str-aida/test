package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.dto.request.LoginRequest;
import com.Trabajo_Final_Beltran.dto.request.RegisterRequest;
import com.Trabajo_Final_Beltran.dto.request.RestablecerPasswordRequest;
import com.Trabajo_Final_Beltran.dto.request.SolicitarRecuperacionRequest;
import com.Trabajo_Final_Beltran.dto.response.AuthResponse;
import com.Trabajo_Final_Beltran.entity.Direccion;
import com.Trabajo_Final_Beltran.entity.Establecimiento;
import com.Trabajo_Final_Beltran.entity.RecuperacionPassword;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.Estado;
import com.Trabajo_Final_Beltran.enums.Rol;
import com.Trabajo_Final_Beltran.enums.TipoOperacion;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.mapper.DireccionMapper;
import com.Trabajo_Final_Beltran.mapper.UsuarioMapper;
import com.Trabajo_Final_Beltran.repository.DireccionRepository;
import com.Trabajo_Final_Beltran.repository.EstablecimientoRepository;
import com.Trabajo_Final_Beltran.repository.RecuperacionPasswordRepository;
import com.Trabajo_Final_Beltran.repository.UsuarioRepository;
import com.Trabajo_Final_Beltran.security.JwtService;
import com.Trabajo_Final_Beltran.service.AuthService;
import com.Trabajo_Final_Beltran.service.EmailService;
import com.Trabajo_Final_Beltran.service.LogSistemaService;
import com.Trabajo_Final_Beltran.util.NumeroUtils;
import com.Trabajo_Final_Beltran.util.TextNormalizerUtil;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import org.springframework.security.authentication.BadCredentialsException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    private final RecuperacionPasswordRepository recuperacionPasswordRepository;

    private final EmailService emailService;

    private final EstablecimientoRepository establecimientoRepository;

    private final DireccionRepository direccionRepository;

    private final LogSistemaService logSistemaService;

    private static final SecureRandom RANDOM = new SecureRandom();


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createPersonal(RegisterRequest request) {
        normalizarRequest(request);
        
        request.setNombre(TextNormalizerUtil.normalizarTexto(request.getNombre()));
        request.setApellido(TextNormalizerUtil.normalizarTexto(request.getApellido()));
        
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El email ya existe");
        }
        if (usuarioRepository.existsByDni(request.getDni())) {
            throw new BusinessException("El DNI ya existe");
        }

        Establecimiento establecimiento = establecimientoRepository.findFirstBy()
                .orElseThrow(() -> new BusinessException("No existe establecimiento"));

        Rol rolAsignado = request.getRol() != null ? request.getRol() : Rol.EMPLEADO;
        if (rolAsignado != Rol.ADMIN && rolAsignado != Rol.EMPLEADO) {
            throw new BusinessException("El rol debe ser ADMIN o EMPLEADO");
        }

        Usuario personal = UsuarioMapper.toEntity(request);
        personal.setPassword(passwordEncoder.encode(request.getPassword()));
        personal.setRol(rolAsignado);
        personal.setEstablecimiento(establecimiento);

        try {
            usuarioRepository.save(personal);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("El email o DNI ya está registrado");
        }

        crearDireccionSiExiste(request, personal);

        logSistemaService.registrarLog(
            "USUARIO",
            personal.getId(),
            personal.getNombre() + " " + personal.getApellido(),
            "Crear usuario",
            "Se creó el usuario " + personal.getNombre() + " " + personal.getApellido()
                + " con rol " + personal.getRol(),
            TipoOperacion.INSERT
        );

        return rolAsignado == Rol.ADMIN
                ? "Admin creado correctamente"
                : "Empleado creado correctamente";
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthResponse login(LoginRequest request) {
        normalizarEmail(request);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);
            return new AuthResponse(token);
        } catch (BadCredentialsException e) {
            log.warn("Login fallido (credenciales incorrectas): {}", request.getEmail());
            throw new BusinessException("Credenciales inválidas");
        } catch (DisabledException e) {
            log.warn("Login fallido (cuenta inactiva): {}", request.getEmail());
            throw new BusinessException("Credenciales inválidas");
        }
    }
    
    private Usuario crearUsuarioAutoregistro(RegisterRequest request, Rol rol) {
        normalizarRequest(request);
        validarUsuario(request);

        Establecimiento establecimiento = establecimientoRepository.findFirstBy()
                .orElseThrow(() -> new BusinessException("No existe un establecimiento registrado"));

        Usuario usuario = UsuarioMapper.toEntity(request);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setEstablecimiento(establecimiento);
        usuario.setRol(rol);
        usuario.setEstado(Estado.ACTIVO);

        try {
            usuarioRepository.save(usuario);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("El email o DNI ya está registrado");
        }

        crearDireccionSiExiste(request, usuario);
        return usuario;
    }
    
    private void normalizarRequest(RegisterRequest request) {
    request.setNombre(TextNormalizerUtil.normalizarTexto(request.getNombre()));
    request.setApellido(TextNormalizerUtil.normalizarTexto(request.getApellido()));
    request.setDni(NumeroUtils.limpiarNumero(request.getDni()));
    request.setTelefono(NumeroUtils.limpiarNumero(request.getTelefono()));
    normalizarEmail(request);
}

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthResponse registerAdmin(RegisterRequest request) {
        
        request.setNombre(TextNormalizerUtil.normalizarTexto(request.getNombre()));
        request.setApellido(TextNormalizerUtil.normalizarTexto(request.getApellido()));
        
        if (usuarioRepository.existsByRol(Rol.ADMIN)) {
            throw new BusinessException("Ya existe un administrador registrado");
        }
        Usuario usuario = crearUsuarioAutoregistro(request, Rol.ADMIN);
        return new AuthResponse(jwtService.generateToken(usuario));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthResponse registerCliente(RegisterRequest request) {
        
        request.setNombre(TextNormalizerUtil.normalizarTexto(request.getNombre()));
        request.setApellido(TextNormalizerUtil.normalizarTexto(request.getApellido()));
        
        Usuario usuario = crearUsuarioAutoregistro(request, Rol.CLIENTE);
        return new AuthResponse(jwtService.generateToken(usuario));
    }

    private void validarUsuario(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
          throw new BusinessException("El email ya está registrado");
        }
        if (usuarioRepository.existsByDni(request.getDni())) {
          throw new BusinessException("El DNI ya está registrado");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String solicitarRecuperacionPassword(SolicitarRecuperacionRequest request) {
        normalizarEmail(request);
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(request.getEmail());
        if (usuarioOpt.isEmpty()) {
            return "Si el correo está registrado, vas a recibir instrucciones.";
        }
        Usuario usuario = usuarioOpt.get();

        List<RecuperacionPassword> codigosPendientes =
                recuperacionPasswordRepository.findByUsuarioAndUsadoFalse(usuario);
        codigosPendientes.forEach(c -> c.setUsado(true));
        recuperacionPasswordRepository.saveAll(codigosPendientes);

        String codigo = generarCodigoRecuperacion();
        RecuperacionPassword recuperacion = RecuperacionPassword.builder()
                .token(hashToken(codigo))
                .usuario(usuario)
                .fechaExpiracion(LocalDateTime.now().plusMinutes(15))
                .usado(false)
                .build();
        recuperacionPasswordRepository.save(recuperacion);
        emailService.enviarEmailRecuperacion(usuario.getEmail(), codigo);
        return "Si el correo está registrado, vas a recibir instrucciones.";
    }

    private String generarCodigoRecuperacion() {
        int codigo = RANDOM.nextInt(10000);
        return String.format("%04d", codigo);
    }

    private String hashToken(String token) {
        return DigestUtils.sha256Hex(token);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String restablecerPassword(RestablecerPasswordRequest request) {
        String tokenHash = hashToken(request.getToken());

        RecuperacionPassword recuperacion = recuperacionPasswordRepository
            .findByToken(tokenHash)
            .orElseThrow(() -> new BusinessException("Código inválido"));

        if (recuperacion.isUsado()) {
            throw new BusinessException("El código ya fue utilizado");
        }
        if (recuperacion.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new BusinessException("El código expiró");
        }

        Usuario usuario = recuperacion.getUsuario();
        usuario.setPassword(passwordEncoder.encode(request.getNuevaPassword()));
        usuarioRepository.save(usuario);

        recuperacion.setUsado(true);
        recuperacionPasswordRepository.save(recuperacion);

        return "Contraseña actualizada correctamente";
    }
    
    private void crearDireccionSiExiste(
            RegisterRequest request,
            Usuario usuario
    ) {

        if (request.getDireccion() == null) {
            return;
        }

        Direccion direccion =
                DireccionMapper.toEntity(
                        request.getDireccion()
                );

        direccion.setUsuario(usuario);
        direccion.setFechaCreacion(
                LocalDateTime.now()
        );

        direccion.setEsPrincipal(true);

        direccionRepository.save(
                direccion
        );
    }

  private void normalizarEmail(RegisterRequest request) {
    request.setEmail(request.getEmail().trim().toLowerCase());
  }

  private void normalizarEmail(LoginRequest request) {
    request.setEmail(request.getEmail().trim().toLowerCase());
  }

  private void normalizarEmail(SolicitarRecuperacionRequest request) {
    request.setEmail(request.getEmail().trim().toLowerCase());
  }
}