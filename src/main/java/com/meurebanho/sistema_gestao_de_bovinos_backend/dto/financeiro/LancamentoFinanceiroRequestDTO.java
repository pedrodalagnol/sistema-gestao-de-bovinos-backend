package com.meurebanho.sistema_gestao_de_bovinos_backend.dto.financeiro;

import com.meurebanho.sistema_gestao_de_bovinos_backend.financeiro.TipoLancamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoFinanceiroRequestDTO(
        @NotBlank(message = "A descrição não pode ser vazia.")
        String descricao,

        @NotNull(message = "O valor não pode ser nulo.")
        @Positive(message = "O valor deve ser positivo.")
        BigDecimal valor,

        @NotNull(message = "O tipo de lançamento não pode ser nulo.")
        TipoLancamento tipo,

        @NotNull(message = "A data do lançamento não pode ser nula.")
        LocalDate dataLancamento,

        String categoria,

        Long loteId // Opcional
) {
}
