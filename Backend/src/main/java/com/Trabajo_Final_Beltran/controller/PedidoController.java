
package com.Trabajo_Final_Beltran.controller;
import com.Trabajo_Final_Beltran.dto.request.AplicarCuponRequest;
import com.Trabajo_Final_Beltran.dto.request.CreatePedidoRequest;
import com.Trabajo_Final_Beltran.dto.response.PedidoDetalleResponse;
import com.Trabajo_Final_Beltran.dto.response.PedidoResponse;
import com.Trabajo_Final_Beltran.dto.response.ValidacionCuponResponse;
import com.Trabajo_Final_Beltran.enums.EstadoPago;
import com.Trabajo_Final_Beltran.enums.MetodoPago;
import com.Trabajo_Final_Beltran.enums.TipoEntrega;
import com.Trabajo_Final_Beltran.service.PedidoService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Trabajo_Final_Beltran.enums.EstadoPedido;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import com.Trabajo_Final_Beltran.dto.response.PageResponse;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

  private final PedidoService pedidoService;

  @PostMapping
  @PreAuthorize("hasRole('CLIENTE')")
  public ResponseEntity<PedidoDetalleResponse> crearPedido(
      @Valid
      @RequestBody CreatePedidoRequest request
  ) {

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(
            pedidoService.crearPedido(request)
        );
  }
  
  @PutMapping("/{id}/aplicar-cupon")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ValidacionCuponResponse> aplicarCupon(
        @PathVariable Long id,
        @Valid
        @RequestBody AplicarCuponRequest request
    ) {

    return ResponseEntity.ok(
        pedidoService.aplicarCupon(id, request)
    );
    }

  @GetMapping
  @PreAuthorize(
      "hasAnyRole('ADMIN','EMPLEADO','CLIENTE')"
  )
  public ResponseEntity<PageResponse<PedidoResponse>> listarPedidos(

      @RequestParam(required = false)
      EstadoPedido estado,

      @RequestParam(required = false)
      TipoEntrega tipoEntrega,

      @RequestParam(required = false)
      EstadoPago estadoPago,

      @RequestParam(required = false)
      MetodoPago metodoPago,

      @RequestParam(required = false)
      String nombreCliente,

      @RequestParam(required = false)
      String numeroPedido,

      @RequestParam(required = false)
      @DateTimeFormat(
          iso = DateTimeFormat.ISO.DATE
      )
      LocalDate fechaDesde,

      @RequestParam(required = false)
      @DateTimeFormat(
          iso = DateTimeFormat.ISO.DATE
      )
      LocalDate fechaHasta,

      @RequestParam(defaultValue = "0")
      int page,

      @RequestParam(defaultValue = "20")
      int size
  ) {

    return ResponseEntity.ok(
        pedidoService.listarPedidos(
            estado,
            tipoEntrega,
            estadoPago,
            metodoPago,
            nombreCliente,
            numeroPedido,
            fechaDesde,
            fechaHasta,
            page,
            size
        )
    );
  }

  @GetMapping("/en-curso")
  @PreAuthorize(
      "hasAnyRole('ADMIN','EMPLEADO')"
  )
  public ResponseEntity<PageResponse<PedidoResponse>> listarPedidosEnCurso(

      @RequestParam(defaultValue = "0")
      int page,

      @RequestParam(defaultValue = "20")
      int size
  ) {

    return ResponseEntity.ok(
        pedidoService.listarPedidosEnCurso(
            page,
            size
        )
    );
  }


  @GetMapping("/{id}")
  @PreAuthorize(
      "hasAnyRole('ADMIN','EMPLEADO','CLIENTE')"
  )
  public ResponseEntity<PedidoDetalleResponse> obtenerPedidoPorId(
      @PathVariable Long id
  ) {

    return ResponseEntity.ok(
        pedidoService.obtenerPedidoPorId(id)
    );
  }

  @PutMapping("/{id}/aceptar")
  @PreAuthorize(
      "hasAnyRole('ADMIN','EMPLEADO')"
  )
  public ResponseEntity<PedidoDetalleResponse> aceptarPedido(
      @PathVariable Long id
  ) {

    return ResponseEntity.ok(
        pedidoService.aceptarPedido(id)
    );
  }

  @PutMapping("/{id}/rechazar")
  @PreAuthorize(
      "hasAnyRole('ADMIN','EMPLEADO')"
  )
  public ResponseEntity<PedidoDetalleResponse> rechazarPedido(
      @PathVariable Long id
  ) {

    return ResponseEntity.ok(
        pedidoService.rechazarPedido(id)
    );
  }

  @PutMapping("/{id}/en-preparacion")
  @PreAuthorize(
      "hasAnyRole('ADMIN','EMPLEADO')"
  )
  public ResponseEntity<PedidoDetalleResponse> pasarAEnPreparacion(
      @PathVariable Long id
  ) {

    return ResponseEntity.ok(
        pedidoService.pasarAEnPreparacion(id)
    );
  }

  @PutMapping("/{id}/listo")
  @PreAuthorize(
      "hasAnyRole('ADMIN','EMPLEADO')"
  )
  public ResponseEntity<PedidoDetalleResponse> marcarComoListo(
      @PathVariable Long id
  ) {

    return ResponseEntity.ok(
        pedidoService.marcarComoListo(id)
    );
  }

  @PutMapping("/{id}/entregado")
  @PreAuthorize(
      "hasAnyRole('ADMIN','EMPLEADO')"
  )
  public ResponseEntity<PedidoDetalleResponse> marcarComoEntregado(
      @PathVariable Long id
  ) {

    return ResponseEntity.ok(
        pedidoService.marcarComoEntregado(id)
    );
  }
}