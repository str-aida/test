package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.dto.response.ClienteAnaliticaResponse;
import com.Trabajo_Final_Beltran.dto.response.ClienteInactivoResponse;
import com.Trabajo_Final_Beltran.dto.response.EstadoPedidoResponse;
import com.Trabajo_Final_Beltran.dto.response.ProductoAnaliticaResponse;
import com.Trabajo_Final_Beltran.dto.response.ResumenEjecutivoResponse;
import com.Trabajo_Final_Beltran.dto.response.VentaMetodoPagoResponse;
import com.Trabajo_Final_Beltran.dto.response.VentaPeriodoResponse;
import com.Trabajo_Final_Beltran.dto.response.VentaTipoEntregaResponse;
import java.util.List;

public interface AnaliticaService {

  ResumenEjecutivoResponse obtenerResumenEjecutivo();

  List<ClienteAnaliticaResponse> obtenerMejoresClientes(
      int limite
  );

  List<EstadoPedidoResponse> obtenerPedidosPorEstado();

  List<ProductoAnaliticaResponse> obtenerProductosMasVendidos(
      int limite
  );

  List<VentaPeriodoResponse> obtenerVentasPorPeriodo(int dias);

  List<ClienteInactivoResponse> obtenerClientesInactivos(int dias);

  List<VentaMetodoPagoResponse> obtenerVentasPorMetodoPago();

  List<ProductoAnaliticaResponse> obtenerProductosMenosVendidos(int dias, int limite);

  List<VentaTipoEntregaResponse> obtenerVentasPorTipoEntrega();
}
