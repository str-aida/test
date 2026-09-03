package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.service.CuponReservaService;
import com.Trabajo_Final_Beltran.dto.request.AplicarCuponRequest;
import com.Trabajo_Final_Beltran.dto.request.CreateDetallePedidoRequest;
import com.Trabajo_Final_Beltran.dto.request.CreatePedidoRequest;
import com.Trabajo_Final_Beltran.dto.response.PedidoDetalleResponse;
import com.Trabajo_Final_Beltran.dto.response.PedidoResponse;
import com.Trabajo_Final_Beltran.dto.response.ValidacionCuponResponse;
import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.entity.DetallePedido;
import com.Trabajo_Final_Beltran.entity.Direccion;
import com.Trabajo_Final_Beltran.entity.Establecimiento;
import com.Trabajo_Final_Beltran.entity.Pedido;
import com.Trabajo_Final_Beltran.entity.Producto;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.EstadoDireccion;
import com.Trabajo_Final_Beltran.enums.EstadoPedido;
import com.Trabajo_Final_Beltran.enums.Rol;
import com.Trabajo_Final_Beltran.enums.TipoEntrega;
import com.Trabajo_Final_Beltran.enums.TipoServicio;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.mapper.PedidoMapper;
import com.Trabajo_Final_Beltran.repository.DireccionRepository;
import com.Trabajo_Final_Beltran.repository.PedidoRepository;
import com.Trabajo_Final_Beltran.repository.ProductoRepository;
import com.Trabajo_Final_Beltran.security.SecurityUtils;
import com.Trabajo_Final_Beltran.service.LogSistemaService;
import com.Trabajo_Final_Beltran.service.NotificacionService;
import com.Trabajo_Final_Beltran.service.PagoService;
import com.Trabajo_Final_Beltran.service.PedidoService;
import com.Trabajo_Final_Beltran.service.StockService;
import com.Trabajo_Final_Beltran.entity.Pago;
import com.Trabajo_Final_Beltran.enums.EstadoPago;
import com.Trabajo_Final_Beltran.enums.MetodoPago;
import com.Trabajo_Final_Beltran.event.PedidoFinalizadoEvent;
import com.Trabajo_Final_Beltran.repository.PagoRepository;
import com.Trabajo_Final_Beltran.service.AplicacionCuponService;
import com.Trabajo_Final_Beltran.service.CanjeCuponService;
import com.Trabajo_Final_Beltran.service.ValidacionCuponService;
import java.time.LocalDate;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import com.Trabajo_Final_Beltran.specification.PedidoSpecification;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import com.Trabajo_Final_Beltran.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoServiceImpl implements PedidoService {

  private final PedidoRepository pedidoRepository;
  private final ProductoRepository productoRepository;
  private final StockService stockService;
  private final PagoRepository pagoRepository;
  private final PagoService pagoService;
  private final DireccionRepository direccionRepository;
  private final LogSistemaService logSistemaService;
  private final NotificacionService notificacionService;
  private final ValidacionCuponService validacionCuponService;
  private final AplicacionCuponService aplicacionCuponService;
  private final CanjeCuponService canjeCuponService;
  private final org.springframework.context.ApplicationEventPublisher eventPublisher;
  private final CuponReservaService cuponReservaService;


  @Override
  public PageResponse<PedidoResponse> listarPedidos(
      EstadoPedido estado,
      TipoEntrega tipoEntrega,
      EstadoPago estadoPago,
      MetodoPago metodoPago,
      String nombreCliente,
      String numeroPedido,
      LocalDate fechaDesde,
      LocalDate fechaHasta,
      int page,
      int size
  ) {

    Usuario usuario = SecurityUtils.obtenerUsuarioAutenticado();

    Pageable pageable = PageRequest.of(
        page,
        size,
        Sort.by(Sort.Direction.DESC, "fechaHora")
    );

    boolean tieneFiltros = estado != null || tipoEntrega != null || estadoPago != null
        || metodoPago != null || fechaDesde != null || fechaHasta != null
        || nombreCliente != null || numeroPedido != null;

    if (usuario.getRol() == Rol.CLIENTE) {
      if (tieneFiltros) {
        throw new BusinessException(
            "Los filtros solo están disponibles para empleados y administradores"
        );
      }

      Page<Pedido> pedidos = pedidoRepository.findAllByUsuarioId(
          usuario.getId(),
          pageable
      );

      return PageResponse.<PedidoResponse>builder()
          .content(
              pedidos.getContent()
                  .stream()
                  .map(PedidoMapper::toResponse)
                  .toList()
          )
          .pagina(pedidos.getNumber())
          .size(pedidos.getSize())
          .totalElementos(pedidos.getTotalElements())
          .totalPaginas(pedidos.getTotalPages())
          .primera(pedidos.isFirst())
          .ultima(pedidos.isLast())
          .build();
    }

    Long establecimientoId = usuario.getEstablecimiento().getId();

    LocalDateTime fechaInicio = null;
    LocalDateTime fechaFin = null;

    if (fechaDesde != null && fechaHasta != null) {
      if (fechaDesde.isAfter(fechaHasta)) {
        throw new BusinessException(
            "La fecha desde no puede ser posterior a la fecha hasta"
        );
      }

      fechaInicio = fechaDesde.atStartOfDay();
      fechaFin = fechaHasta.atTime(23, 59, 59);
    }

    Specification<Pedido> spec = Specification.where(
        PedidoSpecification.establecimientoId(establecimientoId)
    );

    if (numeroPedido != null && !numeroPedido.isBlank()) {
      spec = spec.and(PedidoSpecification.numeroPedido(numeroPedido));
    }

    if (nombreCliente != null && !nombreCliente.isBlank()) {
      spec = spec.and(PedidoSpecification.nombreCliente(nombreCliente));
    }

    if (estado != null) {
      spec = spec.and(PedidoSpecification.estado(estado));
    }

    if (estadoPago != null) {
      spec = spec.and(PedidoSpecification.estadoPago(estadoPago));
    }

    if (metodoPago != null) {
      spec = spec.and(PedidoSpecification.metodoPago(metodoPago));
    }

    if (tipoEntrega != null) {
      spec = spec.and(PedidoSpecification.tipoEntrega(tipoEntrega));
    }

    if (fechaInicio != null && fechaFin != null) {
      spec = spec.and(
          PedidoSpecification.fechaEntre(fechaInicio, fechaFin)
      );
    }

    Page<Pedido> pedidos = pedidoRepository.findAll(
        spec,
        pageable
    );

    return PageResponse.<PedidoResponse>builder()
        .content(
            pedidos.getContent()
                .stream()
                .map(PedidoMapper::toResponse)
                .toList()
        )
        .pagina(pedidos.getNumber())
        .size(pedidos.getSize())
        .totalElementos(pedidos.getTotalElements())
        .totalPaginas(pedidos.getTotalPages())
        .primera(pedidos.isFirst())
        .ultima(pedidos.isLast())
        .build();
  }

  @Override
  public PedidoDetalleResponse obtenerPedidoPorId(Long id) {
    Usuario usuario = SecurityUtils.obtenerUsuarioAutenticado();

    Pedido pedido;
    if (usuario.getRol() == Rol.CLIENTE) {
      pedido = pedidoRepository.findByIdAndUsuarioId(id, usuario.getId())
          .orElseThrow(() -> new BusinessException("Pedido no encontrado"));
    } else if (usuario.getRol() == Rol.ADMIN || usuario.getRol() == Rol.EMPLEADO) {
      Long establecimientoId = usuario.getEstablecimiento().getId();
      pedido = pedidoRepository.findByIdAndEstablecimientoId(id, establecimientoId)
          .orElseThrow(() -> new BusinessException("Pedido no encontrado"));
    } else {
      throw new BusinessException("Rol no autorizado");
    }

    return PedidoMapper.toDetalleResponse(pedido);
  }

  @Override
  @Transactional
  @Bulkhead(name = "crearPedido", fallbackMethod = "crearPedidoFallback")
  public PedidoDetalleResponse crearPedido(CreatePedidoRequest request) {
    Usuario usuario = SecurityUtils.obtenerUsuarioAutenticado();
    Long establecimientoId = usuario.getEstablecimiento().getId();

    Direccion direccionSeleccionada = null;
    Establecimiento establecimiento = usuario.getEstablecimiento();
    if (establecimiento == null) {
      throw new BusinessException("El usuario no tiene un establecimiento asociado.");
    }

    if (establecimiento.getTipoServicio() == TipoServicio.DELIVERY && request.getTipoEntrega() != TipoEntrega.DELIVERY) {
      throw new BusinessException("El establecimiento solo permite pedidos con entrega a delivery.");
    }

    if (establecimiento.getTipoServicio() == TipoServicio.RETIRO && request.getTipoEntrega() != TipoEntrega.RETIRO) {
      throw new BusinessException("El establecimiento solo permite pedidos con retiro en local.");
    }

    if (request.getTipoEntrega() == TipoEntrega.DELIVERY && request.getDireccionId() == null) {
      throw new BusinessException("Debe seleccionar una dirección para delivery");
    }

    String direccionCompleta = null;
    if (request.getTipoEntrega() == TipoEntrega.DELIVERY) {
      direccionSeleccionada = direccionRepository
          .findByIdAndUsuarioIdAndEstado(request.getDireccionId(), usuario.getId(), EstadoDireccion.ACTIVA)
          .orElseThrow(() -> new BusinessException("Dirección no encontrada"));

      direccionCompleta = direccionSeleccionada.getCalle() + " " + direccionSeleccionada.getNumero()
          + ", " + direccionSeleccionada.getLocalidad();
    }

    Pedido pedido = Pedido.builder()
        .usuario(usuario)
        .establecimiento(usuario.getEstablecimiento())
        .fechaHora(LocalDateTime.now())
        .estado(EstadoPedido.PENDIENTE)
        .tipoEntrega(request.getTipoEntrega())
        .metodoPago(request.getMetodoPago())
        .nombreCliente(usuario.getNombre() + " " + usuario.getApellido())
        .telefonoCliente(usuario.getTelefono())
        .direccionCliente(direccionCompleta)
        .build();

    BigDecimal total = BigDecimal.ZERO;

    if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
      throw new BusinessException("El pedido debe contener al menos un producto");
    }

    for (CreateDetallePedidoRequest detalleRequest : request.getDetalles()) {
      Producto producto = productoRepository
          .findByIdAndEstablecimientoId(detalleRequest.getProductoId(), establecimientoId)
          .orElseThrow(() -> new BusinessException("Producto no encontrado"));

      stockService.validarStockDisponible(producto, detalleRequest.getCantidad());

      BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(detalleRequest.getCantidad()));
      total = total.add(subtotal);

      DetallePedido detallePedido = DetallePedido.builder()
          .pedido(pedido)
          .producto(producto)
          .cantidad(detalleRequest.getCantidad())
          .precioUnitario(producto.getPrecio())
          .subtotal(subtotal)
          .nombreProducto(producto.getNombre())
          .build();

      pedido.getDetalles().add(detallePedido);
    }

    pedido.setTotal(total);

    Pedido pedidoGuardado = pedidoRepository.save(pedido);
    pedidoGuardado.setNumeroPedido(String.format("PED-%08d", pedidoGuardado.getId()));
    pedidoGuardado = pedidoRepository.save(pedidoGuardado);

    return PedidoMapper.toDetalleResponse(pedidoGuardado);
  }

  private PedidoDetalleResponse crearPedidoFallback(CreatePedidoRequest request, BulkheadFullException ex) {
    log.warn("Bulkhead lleno en crearPedido - considerar subir el límite");
    throw new BusinessException("Estamos procesando muchos pedidos en este momento. Por favor, intentá de nuevo en unos segundos.");
  }

  @Override
  @Transactional
  public ValidacionCuponResponse aplicarCupon(Long pedidoId, AplicarCuponRequest request) {
    Usuario usuario = SecurityUtils.obtenerUsuarioAutenticado();

    Pedido pedido = pedidoRepository.findByIdAndUsuarioId(pedidoId, usuario.getId())
        .orElseThrow(() -> new BusinessException("Pedido no encontrado"));

    if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
      throw new BusinessException("Solo se puede aplicar un cupón a pedidos pendientes");
    }
    if (pedido.getCupon() != null) {
      throw new BusinessException("Este pedido ya tiene un cupón aplicado");
    }

    Cupon cupon = validacionCuponService.validarCupon(request.getCodigo(), usuario);
    ValidacionCuponResponse resultado = aplicacionCuponService.calcularDescuento(cupon, pedido.getTotal());

    pedido.setCupon(cupon);
    pedido.setMontoDescuento(resultado.getMontoDescuento());
    pedidoRepository.save(pedido);

    cuponReservaService.reservar(cupon, usuario, pedido.getId());

    return resultado;
  }
  
  private PedidoDetalleResponse transicionar(
      Long id,
      EstadoPedido estadoEsperado,
      EstadoPedido nuevoEstado,
      String mensajeError,
      String descripcionAuditoria,
      Consumer<Pedido> accionAntesDeGuardar,
      Consumer<Pedido> notificador
  ) {
    Pedido pedido = obtenerPedidoDelEstablecimiento(id);
    validarEstadoPedido(pedido, estadoEsperado, mensajeError);

    String estadoAnterior = pedido.getEstado().name();

    accionAntesDeGuardar.accept(pedido);

    pedido.setEstado(nuevoEstado);
    Pedido pedidoActualizado = pedidoRepository.save(pedido);

    logSistemaService.registrarAuditoria(
        "PEDIDO",
        pedidoActualizado.getId(),
        pedidoActualizado.getNumeroPedido(),
        "estado",
        estadoAnterior,
        pedidoActualizado.getEstado().name(),
        descripcionAuditoria
    );

    notificador.accept(pedidoActualizado);

    return PedidoMapper.toDetalleResponse(pedidoActualizado);
  }

    @Override
    @Transactional
    public PedidoDetalleResponse aceptarPedido(Long id) {
      return transicionar(
          id,
          EstadoPedido.PENDIENTE,
          EstadoPedido.ACEPTADO,
          "Solo se pueden aceptar pedidos pendientes",
          "Pedido aceptado",
          this::validarPagoYDescontarStock,
          notificacionService::notificarPedidoAceptado
      );
    }

      private void validarPagoYDescontarStock(Pedido pedido) {
          Pago pago = pagoRepository.findByPedidoId(pedido.getId()).orElse(null);

          if (pago != null && pago.getMetodo() == MetodoPago.TARJETA && pago.getEstado() != EstadoPago.APROBADO) {
              throw new BusinessException("El pago debe estar aprobado para aceptar el pedido");
          }

          for (DetallePedido detallePedido : pedido.getDetalles()) {
              stockService.descontarStock(detallePedido.getProducto(), detallePedido.getCantidad());
          }

          if (pedido.getCupon() != null) {
              canjeCuponService.canjear(pedido.getCupon(), pedido.getUsuario());
          }
      }

    @Override
    @Transactional
    public PedidoDetalleResponse rechazarPedido(Long id) {
        Pedido pedido = obtenerPedidoDelEstablecimiento(id);

        if (pedido.getEstado() != EstadoPedido.PENDIENTE && pedido.getEstado() != EstadoPedido.ACEPTADO) {
            throw new BusinessException("Solo se puede rechazar un pedido en estado PENDIENTE o ACEPTADO");
        }

        String estadoAnterior = pedido.getEstado().name();

        if (pedido.getEstado() == EstadoPedido.PENDIENTE && pedido.getCupon() != null) {
            cuponReservaService.liberarReserva(pedido.getCupon(), pedido.getUsuario());
        }

        boolean stockYaDescontado = pedido.getEstado() == EstadoPedido.ACEPTADO;

        Pago pago = pagoRepository.findByPedidoId(pedido.getId()).orElse(null);
        if (pago != null && pago.getEstado() == EstadoPago.APROBADO) {
            pagoService.reembolsarPago(pago.getId());
        } else if (pago != null && pago.getEstado() == EstadoPago.PENDIENTE) {
            pago.setEstado(EstadoPago.ANULADO);
            pago.setFechaActualizacion(LocalDateTime.now());
            pagoRepository.save(pago);
        }

        if (stockYaDescontado) {
            for (DetallePedido d : pedido.getDetalles()) {
                stockService.reponerStock(d.getProducto(), d.getCantidad());
            }
            if (pedido.getCupon() != null) {
                canjeCuponService.deshacerCanje(pedido.getCupon(), pedido.getUsuario());
            }
        }

        pedido.setEstado(EstadoPedido.RECHAZADO);
        Pedido pedidoActualizado = pedidoRepository.save(pedido);

        logSistemaService.registrarAuditoria(
            "PEDIDO",
            pedidoActualizado.getId(),
            pedidoActualizado.getNumeroPedido(),
            "estado",
            estadoAnterior,
            pedidoActualizado.getEstado().name(),
            "Pedido rechazado"
        );

        notificacionService.notificarPedidoRechazado(pedidoActualizado);

        return PedidoMapper.toDetalleResponse(pedidoActualizado);
    }

    @Override
    @Transactional
    public PedidoDetalleResponse pasarAEnPreparacion(Long id) {
      return transicionar(
          id,
          EstadoPedido.ACEPTADO,
          EstadoPedido.EN_PREPARACION,
          "Solo se pueden pasar a preparación pedidos aceptados",
          "Pedido en preparación",
          p -> { },
          notificacionService::notificarPedidoEnPreparacion
      );
    }

    @Override
    @Transactional
    public PedidoDetalleResponse marcarComoListo(Long id) {
      return transicionar(
          id,
          EstadoPedido.EN_PREPARACION,
          EstadoPedido.LISTO,
          "Solo se pueden marcar como listos pedidos en preparación",
          "Pedido listo",
          p -> { },
          notificacionService::notificarPedidoListo
      );
    }

    @Override
    @Transactional
    public PedidoDetalleResponse marcarComoEntregado(Long id) {
      return transicionar(
          id,
          EstadoPedido.LISTO,
          EstadoPedido.ENTREGADO,
          "Solo se pueden entregar pedidos listos",
          "Pedido entregado",
          this::validarPagoYConfirmarEfectivo,
          this::publicarEventoYNotificarEntrega
      );
    }

    private void validarPagoYConfirmarEfectivo(Pedido pedido) {
      Pago pago = pedido.getPago();
      if (pago == null) {
        throw new BusinessException("El pedido no tiene un pago asociado, no se puede marcar como entregado");
      }

      // tarjeta debe estar APROBADA antes de entregar (mismo criterio que aceptarPedido).
      if (pago.getMetodo() == MetodoPago.TARJETA && pago.getEstado() != EstadoPago.APROBADO) {
        throw new BusinessException("El pago con tarjeta debe estar aprobado para entregar el pedido");
      }

      if (pago.getMetodo() == MetodoPago.EFECTIVO) {
        pago.setEstado(EstadoPago.APROBADO);
        pago.setFechaActualizacion(LocalDateTime.now());
      }
    }

    private void publicarEventoYNotificarEntrega(Pedido pedidoActualizado) {
      Usuario usuario = pedidoActualizado.getUsuario();
      usuario.getEmail();   // fuerza init del proxy AHORA, con la sesión abierta
      usuario.getNombre();

      eventPublisher.publishEvent(new PedidoFinalizadoEvent(this, pedidoActualizado.getUsuario()));
      notificacionService.notificarPedidoEntregado(pedidoActualizado);
    }

    private Pedido obtenerPedidoDelEstablecimiento(Long id) {
      Usuario usuario = SecurityUtils.obtenerUsuarioAutenticado();
      Long establecimientoId = usuario.getEstablecimiento().getId();
      return pedidoRepository.findByIdAndEstablecimientoId(id, establecimientoId)
          .orElseThrow(() -> new BusinessException("Pedido no encontrado"));
    }

  @Override
  public PageResponse<PedidoResponse> listarPedidosEnCurso(
      int page,
      int size
  ) {

    Usuario usuario = SecurityUtils.obtenerUsuarioAutenticado();

    Long establecimientoId = usuario.getEstablecimiento().getId();

    Pageable pageable = PageRequest.of(
        page,
        size,
        Sort.by(Sort.Direction.DESC, "fechaHora")
    );

    Specification<Pedido> spec = Specification.where(
        PedidoSpecification.establecimientoId(establecimientoId)
    ).and(
        PedidoSpecification.pedidosEnCurso()
    );

    Page<Pedido> pedidos = pedidoRepository.findAll(
        spec,
        pageable
    );

    return PageResponse.<PedidoResponse>builder()
        .content(
            pedidos.getContent()
                .stream()
                .map(PedidoMapper::toResponse)
                .toList()
        )
        .pagina(pedidos.getNumber())
        .size(pedidos.getSize())
        .totalElementos(pedidos.getTotalElements())
        .totalPaginas(pedidos.getTotalPages())
        .primera(pedidos.isFirst())
        .ultima(pedidos.isLast())
        .build();
  }

    private void validarEstadoPedido(Pedido pedido, EstadoPedido estadoEsperado, String mensajeError) {
      if (pedido.getEstado() != estadoEsperado) {
        throw new BusinessException(mensajeError);
      }
      
    }
}