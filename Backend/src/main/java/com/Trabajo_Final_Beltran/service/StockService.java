package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.entity.Producto;

public interface StockService {

    void validarStockDisponible(
            Producto producto,
            Integer cantidad
    );

    void descontarStock(
            Producto producto,
            Integer cantidad
    );
    
    void reponerStock(
            Producto producto, Integer cantidad
    );
   
}