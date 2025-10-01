package com.meurebanho.sistema_gestao_de_bovinos_backend.dto.financeiro;

import java.math.BigDecimal;

public record RelatorioFluxoCaixaDTO(
        BigDecimal totalReceitas,
        BigDecimal totalDespesas,
        BigDecimal saldoFinal
) {
}
