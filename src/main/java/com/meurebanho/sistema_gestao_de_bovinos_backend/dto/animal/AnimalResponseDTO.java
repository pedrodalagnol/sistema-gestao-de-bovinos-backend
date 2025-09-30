package com.meurebanho.sistema_gestao_de_bovinos_backend.dto.animal;

import java.util.Date;

// DTO para enviar dados de um animal para o frontend
public record AnimalResponseDTO(
        Long id,
        String identificador,
        String sexo,
        String raca,
        Date dataNascimento,
        String status
) {}