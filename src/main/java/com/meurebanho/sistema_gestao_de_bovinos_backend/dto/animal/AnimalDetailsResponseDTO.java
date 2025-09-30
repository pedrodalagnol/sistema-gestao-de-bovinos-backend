package com.meurebanho.sistema_gestao_de_bovinos_backend.dto.animal;

import com.meurebanho.sistema_gestao_de_bovinos_backend.animal.EventoAnimal;

import java.util.Date;
import java.util.List;

// DTO completo com dados do animal, seu histórico e o GMD
public record AnimalDetailsResponseDTO(
        Long id,
        String identificador,
        String sexo,
        String raca,
        Date dataNascimento,
        String status,
        double gmd, // Ganho de Peso Médio Diário
        List<EventoAnimal> historico
) {}