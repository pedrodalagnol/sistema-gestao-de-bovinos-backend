package com.meurebanho.sistema_gestao_de_bovinos_backend.dto.estoque;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MovimentacaoEstoqueRequestDTO(
        @NotNull(message = "A quantidade não pode ser nula.")
        @Positive(message = "A quantidade deve ser um valor positivo.")
        Double quantidade
) {
}
