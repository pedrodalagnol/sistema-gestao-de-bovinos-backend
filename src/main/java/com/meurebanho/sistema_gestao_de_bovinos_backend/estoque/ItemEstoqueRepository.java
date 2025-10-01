package com.meurebanho.sistema_gestao_de_bovinos_backend.estoque;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemEstoqueRepository extends JpaRepository<ItemEstoque, Long> {
    List<ItemEstoque> findByFazendaId(Long fazendaId);
}
