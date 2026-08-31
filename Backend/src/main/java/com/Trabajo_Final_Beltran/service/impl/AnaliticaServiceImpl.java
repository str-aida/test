package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.dto.response.ClienteAnaliticaResponse;
import com.Trabajo_Final_Beltran.dto.response.ClienteInactivoResponse;
import com.Trabajo_Final_Beltran.dto.response.EstadoPedidoResponse;
import com.Trabajo_Final_Beltran.dto.response.ProductoAnaliticaResponse;
import com.Trabajo_Final_Beltran.dto.response.ResumenEjecutivoResponse;
import com.Trabajo_Final_Beltran.dto.response.VentaMetodoPagoResponse;
import com.Trabajo_Final_Beltran.dto.response.VentaPeriodoResponse;
import com.Trabajo_Final_Beltran.dto.response.VentaTipoEntregaResponse;
import com.Trabajo_Final_Beltran.enums.EstadoPedido;
import com.Trabajo_Final_Beltran.enums.EstadoProducto;
import com.Trabajo_Final_Beltran.enums.Rol;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.repository.DetallePedidoRepository;
import com.Trabajo_Final_Beltran.repository.PedidoRepository;
import com.Trabajo_Final_Beltran.repository.UsuarioRepository;
import com.Trabajo_Final_Beltran.service.AnaliticaService;
import java.time.LocalDateTime;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnaliticaServiceImpl implements AnaliticaService {

  private final PedidoRepository pedidoRepository;

  private final UsuarioRepository usuarioRepository;

  private final DetallePedidoRepository detallePedidoRepository;


  @Override
  public ResumenEjecutivoResponse obtenerResumenEjecutivo() {

    long totalPedidos =
        pedidoRepository.count();

    BigDecimal ventasTotales =
        pedidoRepository.obtenerVentasTotales();

    BigDecimal ticketPromedio =
        pedidoRepository.obtenerTicketPromedio();

    long pedidosPendientes =
        pedidoRepository.countByEstado(
            EstadoPedido.PENDIENTE
        );

    long pedidosEntregados =
        pedidoRepository.countByEstado(
            EstadoPedido.ENTREGADO
        );

    long clientesRegistrados =
        usuarioRepository.countByRol(
            Rol.CLIENTE
        );

    return ResumenEjecutivoResponse.builder()
        .totalPedidos(
            totalPedidos
        )
        .ventasTotales(
            ventasTotales
        )
        .ticketPromedio(
            ticketPromedio
        )
        .pedidosPendientes(
            pedidosPendientes
        )
        .pedidosEntregados(
            pedidosEntregados
        )
        .clientesRegistrados(
            clientesRegistrados
        )
        .build();
  }

  @Override
  public List<ClienteAnaliticaResponse> obtenerMejoresClientes(int limite) {

    validarRango(
        limite,
        1,
        100,
        "límite"
    );

    Pageable pageable =
        PageRequest.of(
            0,
            limite
        );

    return pedidoRepository
        .obtenerMejoresClientes(
            pageable
        );
  }

  @Override
  public List<EstadoPedidoResponse> obtenerPedidosPorEstado() {

    List<EstadoPedidoResponse> pedidosPorEstado =
        pedidoRepository.obtenerPedidosPorEstado();

    Map<EstadoPedido, Long> cantidadesPorEstado =
        pedidosPorEstado.stream()
            .collect(
                Collectors.toMap(
                    EstadoPedidoResponse::getEstado,
                    EstadoPedidoResponse::getCantidad
                )
            );
    List<EstadoPedidoResponse> resultado =
        Arrays.stream(
                EstadoPedido.values()
            )
            .map(estado ->
                EstadoPedidoResponse.builder()
                    .estado(
                        estado
                    )
                    .cantidad(
                        cantidadesPorEstado.getOrDefault(
                            estado,
                            0L
                        )
                    )
                    .build()
            )
            .toList();

    return resultado;

  }

  @Override
  public List<ProductoAnaliticaResponse> obtenerProductosMasVendidos(int limite) {

    validarRango(
        limite,
        1,
        100,
        "límite"
    );

    Pageable pageable =
        PageRequest.of(
            0,
            limite
        );

    return detallePedidoRepository
        .obtenerProductosMasVendidos(
            pageable
        );
  }

  @Override
  public List<VentaPeriodoResponse> obtenerVentasPorPeriodo(int dias) {

    validarRango(
        dias,
        1,
        365,
        "días"
    );

    LocalDateTime fechaDesde =
        LocalDateTime.now()
            .minusDays(
                dias
            );

    List<Object[]> resultados =
        pedidoRepository.obtenerVentasPorPeriodo(
            fechaDesde
        );

    return resultados.stream()
        .map(resultado ->
            VentaPeriodoResponse.builder()
                .periodo(
                    resultado[0].toString()
                )
                .ventas(
                    (BigDecimal) resultado[1]
                )
                .build()
        )
        .toList();
  }

  private void validarRango(
      int valor,
      int minimo,
      int maximo,
      String nombreParametro
  ) {

    if (
        valor < minimo
            || valor > maximo
    ) {

      throw new BusinessException(
          "El parámetro "
              + nombreParametro
              + " debe estar entre "
              + minimo
              + " y "
              + maximo
              + "."
      );

    }

  }

  @Override
  public List<ClienteInactivoResponse>
  obtenerClientesInactivos(int dias) {

    validarRango(
        dias,
        1,
        365,
        "días"
    );

    LocalDateTime fechaLimite =
        LocalDateTime.now()
            .minusDays(
                dias
            );

    List<Object[]> resultados =
        pedidoRepository.obtenerClientesInactivos(
            fechaLimite
        );

    return resultados.stream()
        .map(resultado ->
            ClienteInactivoResponse.builder()
                .idCliente(
                    ((Number) resultado[0]).longValue()
                )
                .nombreCompleto(
                    resultado[1].toString()
                )
                .email(
                    resultado[2].toString()
                )
                .ultimaCompra(
                    (LocalDateTime) resultado[3]
                )
                .montoUltimaCompra(
                    (BigDecimal) resultado[4]
                )
                .totalGastado(
                    (BigDecimal) resultado[5]
                )
                .cantidadPedidos(
                    ((Number) resultado[6]).longValue()
                )
                .build()
        )
        .toList();
  }
  @Override
  public List<VentaMetodoPagoResponse>
  obtenerVentasPorMetodoPago() {

    return pedidoRepository
        .obtenerVentasPorMetodoPago();
  }

  @Override
  public List<ProductoAnaliticaResponse>
  obtenerProductosMenosVendidos(
      int dias,
      int limite
  ) {

    validarRango(
        dias,
        1,
        365,
        "días"
    );

    validarRango(
        limite,
        1,
        100,
        "límite"
    );

    LocalDateTime fechaDesde =
        LocalDateTime.now()
            .minusDays(
                dias
            );

    List<Object[]> resultados =
        detallePedidoRepository
            .obtenerProductosMenosVendidos(
                fechaDesde,
                limite
            );

    return resultados.stream()
        .map(resultado ->
            ProductoAnaliticaResponse.builder()
                .idProducto(
                    ((Number) resultado[0]).longValue()
                )
                .nombreProducto(
                    resultado[1].toString()
                )
                .cantidadVendida(
                    ((Number) resultado[2]).longValue()
                )
                .totalVendido(
                    (BigDecimal) resultado[3]
                )
                .estado(
                    EstadoProducto.valueOf(
                        resultado[4].toString()
                    )
                )
                .build()
        )
        .toList();
  }

  @Override
  public List<VentaTipoEntregaResponse> obtenerVentasPorTipoEntrega() {

    return pedidoRepository.obtenerVentasPorTipoEntrega();
  }
  }

