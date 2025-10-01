package com.meurebanho.sistema_gestao_de_bovinos_backend.dto.pasto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PastoRequestDTO(
        @NotBlank(message = "O nome do pasto não pode ser vazio.")
        String nome,

        @NotNull(message = "A área em hectares não pode ser nula.")
        @Positive(message = "A área deve ser um valor positivo.")
        Double areaHa
) {
}
