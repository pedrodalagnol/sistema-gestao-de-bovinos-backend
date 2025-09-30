package com.meurebanho.sistema_gestao_de_bovinos_backend.dto.lote;

// Este DTO pode evoluir para incluir a contagem de animais, etc.
public record LoteResponseDTO(Long id, String nome, String descricao) {}