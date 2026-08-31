package com.Trabajo_Final_Beltran.controller;

import com.Trabajo_Final_Beltran.dto.response.ClienteAnaliticaResponse;
import com.Trabajo_Final_Beltran.dto.response.ClienteInactivoResponse;
import com.Trabajo_Final_Beltran.dto.response.EstadoPedidoResponse;
import com.Trabajo_Final_Beltran.dto.response.ProductoAnaliticaResponse;
import com.Trabajo_Final_Beltran.dto.response.ResumenEjecutivoResponse;
import com.Trabajo_Final_Beltran.dto.response.VentaMetodoPagoResponse;
import com.Trabajo_Final_Beltran.dto.response.VentaPeriodoResponse;
import com.Trabajo_Final_Beltran.dto.response.VentaTipoEntregaResponse;
import com.Trabajo_Final_Beltran.service.AnaliticaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analitica")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AnaliticaController {

  private final AnaliticaService analiticaService;

  @GetMapping("/resumen")
  public ResponseEntity<ResumenEjecutivoResponse> obtenerResumenEjecutivo() {

    return ResponseEntity.ok(
        analiticaService.obtenerResumenEjecutivo()
    );
  }

  @GetMapping("/clientes/mejores")
  public ResponseEntity<List<ClienteAnaliticaResponse>>
  obtenerMejoresClientes(
      @RequestParam(defaultValue = "5")
      int limite
  ) {

    return ResponseEntity.ok(
        analiticaService.obtenerMejoresClientes(
            limite
        )
    );
  }

  @GetMapping("/pedidos/estados")
  public ResponseEntity<List<EstadoPedidoResponse>>
  obtenerPedidosPorEstado() {

    return ResponseEntity.ok(
        analiticaService.obtenerPedidosPorEstado()
    );
  }

  @GetMapping("/productos/mas-vendidos")
  public ResponseEntity<List<ProductoAnaliticaResponse>>
  obtenerProductosMasVendidos(
      @RequestParam(defaultValue = "5")
      int limite
  ) {

    return ResponseEntity.ok(
        analiticaService.obtenerProductosMasVendidos(
            limite
        )
    );
  }

  @GetMapping("/ventas/periodo")
  public ResponseEntity<List<VentaPeriodoResponse>>
  obtenerVentasPorPeriodo(
      @RequestParam(defaultValue = "30")
      int dias
  ) {

    return ResponseEntity.ok(
        analiticaService.obtenerVentasPorPeriodo(
            dias
        )
    );
  }

  @GetMapping("/clientes/inactivos")
  public ResponseEntity<List<ClienteInactivoResponse>>
  obtenerClientesInactivos(
      @RequestParam(defaultValue = "30")
      int dias
  ) {

    return ResponseEntity.ok(
        analiticaService.obtenerClientesInactivos(
            dias
        )
    );
  }

  @GetMapping("/ventas/metodos-pago")
  public ResponseEntity<List<VentaMetodoPagoResponse>>
  obtenerVentasPorMetodoPago() {

    return ResponseEntity.ok(
        analiticaService.obtenerVentasPorMetodoPago()
    );
  }

  @GetMapping("/productos/menos-vendidos")
  public ResponseEntity<List<ProductoAnaliticaResponse>>
  obtenerProductosMenosVendidos(
      @RequestParam(defaultValue = "30")
      int dias,

      @RequestParam(defaultValue = "5")
      int limite
  ) {

    return ResponseEntity.ok(
        analiticaService.obtenerProductosMenosVendidos(
            dias,
            limite
        )
    );
  }

  @GetMapping("/ventas/tipos-entrega")
  public ResponseEntity<List<VentaTipoEntregaResponse>>
  obtenerVentasPorTipoEntrega() {

    return ResponseEntity.ok(
        analiticaService.obtenerVentasPorTipoEntrega()
    );
  }
}