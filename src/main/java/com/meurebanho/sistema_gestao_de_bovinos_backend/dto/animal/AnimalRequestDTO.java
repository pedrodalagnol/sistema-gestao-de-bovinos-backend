package com.meurebanho.sistema_gestao_de_bovinos_backend.dto.animal;

import java.util.Date;

// DTO para criar ou atualizar um animal
public record AnimalRequestDTO(
        String identificador,
        String sexo,
        String raca,
        Date dataNascimento
) {}