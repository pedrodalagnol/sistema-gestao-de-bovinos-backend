package com.meurebanho.sistema_gestao_de_bovinos_backend.dto.auth;

public record CadastroRequestDTO(
        String nomeFazenda,
        String nomeUsuario,
        String email,
        String senha
) {}