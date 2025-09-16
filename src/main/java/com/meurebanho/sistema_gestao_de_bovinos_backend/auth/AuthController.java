package com.meurebanho.sistema_gestao_de_bovinos_backend.auth;

import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.auth.AuthResponseDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.auth.CadastroRequestDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.auth.LoginRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registrar")
    public ResponseEntity<AuthResponseDTO> registrar(@RequestBody CadastroRequestDTO request) {
        return ResponseEntity.ok(authService.registrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}