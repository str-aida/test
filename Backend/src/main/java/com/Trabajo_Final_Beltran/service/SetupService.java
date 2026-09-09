package com.Trabajo_Final_Beltran.service;

import com.Trabajo_Final_Beltran.dto.request.SetupRequest;
import com.Trabajo_Final_Beltran.dto.response.SetupResponse;

public interface SetupService {

  SetupResponse crearEstablecimiento(
      SetupRequest request
  );
}