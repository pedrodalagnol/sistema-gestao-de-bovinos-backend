package com.meurebanho.sistema_gestao_de_bovinos_backend.dto.estoque;

import com.meurebanho.sistema_gestao_de_bovinos_backend.estoque.UnidadeMedida;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ItemEstoqueRequestDTO(
        @NotBlank(message = "O nome do item não pode ser vazio.")
        String nome,

        String categoria,

        @NotNull(message = "A unidade de medida não pode ser nula.")
        UnidadeMedida unidadeMedida,

        @NotNull(message = "A quantidade inicial não pode ser nula.")
        @PositiveOrZero(message = "A quantidade deve ser zero ou maior.")
        Double quantidadeInicial
) {
}
