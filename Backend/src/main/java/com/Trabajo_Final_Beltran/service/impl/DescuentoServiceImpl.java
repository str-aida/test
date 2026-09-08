package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.dto.request.CreateDescuentoRequest;
import com.Trabajo_Final_Beltran.dto.request.ProductoDescuentoRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdateDescuentoRequest;
import com.Trabajo_Final_Beltran.dto.response.DescuentoResponse;
import com.Trabajo_Final_Beltran.dto.response.MensajeResponse;
import com.Trabajo_Final_Beltran.entity.Descuento;
import com.Trabajo_Final_Beltran.entity.DescuentoProducto;
import com.Trabajo_Final_Beltran.entity.Establecimiento;
import com.Trabajo_Final_Beltran.entity.Producto;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.EstadoDescuento;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.mapper.DescuentoMapper;
import com.Trabajo_Final_Beltran.repository.DescuentoProductoRepository;
import com.Trabajo_Final_Beltran.repository.DescuentoRepository;
import com.Trabajo_Final_Beltran.repository.ProductoRepository;
import com.Trabajo_Final_Beltran.security.SecurityUtils;
import com.Trabajo_Final_Beltran.service.DescuentoService;
import com.Trabajo_Final_Beltran.service.LogSistemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class DescuentoServiceImpl implements DescuentoService {

    private final DescuentoRepository descuentoRepository;
    private final DescuentoProductoRepository descuentoProductoRepository;
    private final ProductoRepository productoRepository;
    private final LogSistemaService logSistemaService;

    @Override
    @Transactional
    public DescuentoResponse crearDescuento(CreateDescuentoRequest request) {
        Usuario usuario = SecurityUtils.obtenerUsuarioAutenticado();
        Establecimiento establecimiento = usuario.getEstablecimiento();

        validarRangoFechas(request.getFechaInicio(), request.getFechaFin());

        Descuento descuento = DescuentoMapper.toEntity(request, establecimiento);

        for (ProductoDescuentoRequest productoRequest : request.getProductos()) {
            Producto producto = obtenerProductoDelEstablecimiento(
                    productoRequest.getProductoId(), establecimiento.getId());

            validarSinSolapamiento(
                    producto.getId(),
                    request.getFechaInicio(),
                    request.getFechaFin(),
                    -1L 
            );

            descuento.getProductos().add(
                    DescuentoMapper.toDescuentoProducto(productoRequest, descuento, producto)
            );
        }

        Descuento guardado = descuentoRepository.save(descuento);

        logSistemaService.registrarLog(
                "DESCUENTO",
                guardado.getId(),
                guardado.getNombre(),
                "Crear descuento",
                "Se creó el descuento " + guardado.getNombre(),
                com.Trabajo_Final_Beltran.enums.TipoOperacion.INSERT
        );

        return DescuentoMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public DescuentoResponse editarDescuento(Long id, UpdateDescuentoRequest request) {
        Usuario usuario = SecurityUtils.obtenerUsuarioAutenticado();
        Establecimiento establecimiento = usuario.getEstablecimiento();

        Descuento descuento = obtenerDescuentoDelEstablecimiento(id, establecimiento.getId());

        validarRangoFechas(request.getFechaInicio(), request.getFechaFin());

        DescuentoMapper.updateFromRequest(descuento, request);

        Map<Long, DescuentoProducto> existentesPorProducto = descuento.getProductos().stream()
                .collect(Collectors.toMap(dp -> dp.getProducto().getId(), dp -> dp, (a, b) -> a));

        Set<Long> nuevosProductoIds = request.getProductos().stream()
                .map(ProductoDescuentoRequest::getProductoId)
                .collect(Collectors.toSet());

        descuento.getProductos().removeIf(dp -> !nuevosProductoIds.contains(dp.getProducto().getId()));

        for (ProductoDescuentoRequest productoRequest : request.getProductos()) {
            Producto producto = obtenerProductoDelEstablecimiento(
                    productoRequest.getProductoId(), establecimiento.getId());

            validarSinSolapamiento(
                    producto.getId(),
                    request.getFechaInicio(),
                    request.getFechaFin(),
                    descuento.getId() 
            );

            DescuentoProducto dpExistente = existentesPorProducto.get(producto.getId());
            if (dpExistente != null) {
                dpExistente.setPorcentaje(productoRequest.getPorcentaje());
            } else {
                descuento.getProductos().add(
                        DescuentoMapper.toDescuentoProducto(productoRequest, descuento, producto)
                );
            }
        }

        Descuento actualizado = descuentoRepository.save(descuento);

        return DescuentoMapper.toResponse(actualizado);
    }

    @Override
    public List<DescuentoResponse> listarDescuentos() {
        Usuario usuario = SecurityUtils.obtenerUsuarioAutenticado();
        Long establecimientoId = usuario.getEstablecimiento().getId();

        return descuentoRepository.findAllByEstablecimientoId(establecimientoId)
                .stream()
                .map(DescuentoMapper::toResponse)
                .toList();
    }

    @Override
    public DescuentoResponse obtenerPorId(Long id) {
        Usuario usuario = SecurityUtils.obtenerUsuarioAutenticado();
        Long establecimientoId = usuario.getEstablecimiento().getId();

        Descuento descuento = obtenerDescuentoDelEstablecimiento(id, establecimientoId);
        return DescuentoMapper.toResponse(descuento);
    }

    @Override
    @Transactional
    public MensajeResponse activar(Long id) {
        return cambiarEstado(id, EstadoDescuento.ACTIVO, "activado");
    }

    @Override
    @Transactional
    public MensajeResponse desactivar(Long id) {
        return cambiarEstado(id, EstadoDescuento.INACTIVO, "desactivado");
    }

    private MensajeResponse cambiarEstado(Long id, EstadoDescuento nuevoEstado, String descripcionAccion) {
        Usuario usuario = SecurityUtils.obtenerUsuarioAutenticado();
        Long establecimientoId = usuario.getEstablecimiento().getId();

        Descuento descuento = obtenerDescuentoDelEstablecimiento(id, establecimientoId);

        String estadoAnterior = descuento.getEstado().name();

        descuento.setEstado(nuevoEstado);
        Descuento actualizado = descuentoRepository.save(descuento);

        logSistemaService.registrarAuditoria(
                "DESCUENTO",
                actualizado.getId(),
                actualizado.getNombre(),
                "estado",
                estadoAnterior,
                actualizado.getEstado().name(),
                "Descuento " + descripcionAccion
        );

        return MensajeResponse.builder()
                .mensaje("Descuento " + descripcionAccion + " correctamente")
                .build();
    }

    private Descuento obtenerDescuentoDelEstablecimiento(Long id, Long establecimientoId) {
        return descuentoRepository.findByIdAndEstablecimientoId(id, establecimientoId)
                .orElseThrow(() -> new BusinessException("Descuento no encontrado"));
    }

    private Producto obtenerProductoDelEstablecimiento(Long productoId, Long establecimientoId) {
        return productoRepository.findByIdAndEstablecimientoId(productoId, establecimientoId)
                .orElseThrow(() -> new BusinessException("Producto no encontrado o no pertenece al establecimiento"));
    }

    private void validarRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio.isAfter(fechaFin)) {
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
    }

    private void validarSinSolapamiento(Long productoId, LocalDate fechaInicio, LocalDate fechaFin, Long descuentoIdExcluir) {
        List<DescuentoProducto> solapados = descuentoProductoRepository.findSolapadosPorProducto(
                productoId, fechaInicio, fechaFin, descuentoIdExcluir
        );

        if (!solapados.isEmpty()) {
            throw new BusinessException(
                    "El producto ya tiene un descuento activo en ese rango de fechas"
            );
        }
    }
}