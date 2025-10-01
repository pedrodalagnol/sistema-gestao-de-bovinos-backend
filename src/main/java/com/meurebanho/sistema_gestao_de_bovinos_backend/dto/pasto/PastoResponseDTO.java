package com.meurebanho.sistema_gestao_de_bovinos_backend.dto.pasto;

import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.lote.LoteResponseDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.pasto.PastoStatus;

import java.time.LocalDate;

public record PastoResponseDTO(
        Long id,
        String nome,
        Double areaHa,
        PastoStatus status,
        LocalDate dataInicioStatus,
        LoteResponseDTO loteAlocado,
        Double taxaOcupacao
) {
}
