package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.entity.Producto;
import com.Trabajo_Final_Beltran.enums.EstadoProducto;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.repository.ProductoRepository;
import com.Trabajo_Final_Beltran.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final ProductoRepository productoRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void descontarStock(Producto producto, Integer cantidad) {
        validarStockDisponible(producto, cantidad);

        producto.setStock(producto.getStock() - cantidad);

        if (producto.getStock() <= 0) {
            producto.setEstado(EstadoProducto.INACTIVO);
        }

        productoRepository.save(producto);  
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reponerStock(Producto producto, Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new BusinessException("La cantidad a reponer debe ser mayor a 0");
        }

        producto.setStock(producto.getStock() + cantidad);

        // inverso de descontarStock: si fue inactivado por quedarse sin stock, reactivarlo
        if (producto.getStock() > 0
                && producto.getEstado() == EstadoProducto.INACTIVO) {
            producto.setEstado(EstadoProducto.ACTIVO);
        }

        productoRepository.save(producto);
    }

    @Override
    public void validarStockDisponible(Producto producto, Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new BusinessException("La cantidad debe ser mayor a 0");
        }
        if (producto.getEstado() != EstadoProducto.ACTIVO) {
            throw new BusinessException("El producto " + producto.getNombre() + " no está disponible");
        }
        if (producto.getStock() < cantidad) {
            throw new BusinessException("Stock insuficiente para " + producto.getNombre());
        }
    }
}