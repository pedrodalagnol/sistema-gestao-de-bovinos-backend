package com.meurebanho.sistema_gestao_de_bovinos_backend.dto.estoque;

import com.meurebanho.sistema_gestao_de_bovinos_backend.estoque.UnidadeMedida;

public record ItemEstoqueResponseDTO(
        Long id,
        String nome,
        String categoria,
        UnidadeMedida unidadeMedida,
        Double quantidadeAtual
) {
}
