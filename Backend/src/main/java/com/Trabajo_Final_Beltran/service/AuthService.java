package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.dto.request.LoginRequest;
import com.Trabajo_Final_Beltran.dto.request.RegisterRequest;
import com.Trabajo_Final_Beltran.dto.request.RestablecerPasswordRequest;
import com.Trabajo_Final_Beltran.dto.request.SolicitarRecuperacionRequest;
import com.Trabajo_Final_Beltran.dto.response.AuthResponse;

public interface AuthService {

    String createPersonal(RegisterRequest request);
    
    AuthResponse login(LoginRequest request);

    AuthResponse registerAdmin(RegisterRequest request);

    AuthResponse registerCliente(RegisterRequest request);

    String solicitarRecuperacionPassword(SolicitarRecuperacionRequest request);

    String restablecerPassword(RestablecerPasswordRequest request);
    
    
}