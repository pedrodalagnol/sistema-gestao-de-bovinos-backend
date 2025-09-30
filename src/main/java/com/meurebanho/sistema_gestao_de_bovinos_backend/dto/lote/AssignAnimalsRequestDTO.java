package com.meurebanho.sistema_gestao_de_bovinos_backend.dto.lote;

import java.util.List;

public record AssignAnimalsRequestDTO(List<Long> animalIds) {}