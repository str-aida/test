package com.Trabajo_Final_Beltran.controller;

import com.Trabajo_Final_Beltran.dto.request.SetupRequest;
import com.Trabajo_Final_Beltran.dto.response.SetupResponse;
import com.Trabajo_Final_Beltran.service.SetupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/setup")
@RequiredArgsConstructor
public class SetupController {

    private final SetupService setupService;
    
    @PostMapping("/establecimiento")
    public ResponseEntity<SetupResponse>
    crearEstablecimiento(
            @RequestBody SetupRequest request
    ) {

        return ResponseEntity.ok(
                setupService.crearEstablecimiento(
                        request
                )
        );
    }

}