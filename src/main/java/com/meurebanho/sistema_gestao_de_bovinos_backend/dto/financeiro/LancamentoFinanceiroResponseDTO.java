package com.meurebanho.sistema_gestao_de_bovinos_backend.dto.financeiro;

import com.meurebanho.sistema_gestao_de_bovinos_backend.financeiro.TipoLancamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoFinanceiroResponseDTO(
        Long id,
        String descricao,
        BigDecimal valor,
        TipoLancamento tipo,
        LocalDate dataLancamento,
        String categoria,
        Long loteId
) {
}
