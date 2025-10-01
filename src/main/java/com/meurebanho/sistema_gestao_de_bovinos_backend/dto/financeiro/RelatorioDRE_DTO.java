package com.meurebanho.sistema_gestao_de_bovinos_backend.dto.financeiro;

import java.math.BigDecimal;
import java.util.Map;

public record RelatorioDRE_DTO(
        BigDecimal totalReceitas,
        Map<String, BigDecimal> despesasPorCategoria,
        BigDecimal totalDespesas,
        BigDecimal resultadoLiquido // Lucro ou Prejuízo
) {
}
