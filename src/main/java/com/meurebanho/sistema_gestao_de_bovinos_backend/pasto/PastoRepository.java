package com.meurebanho.sistema_gestao_de_bovinos_backend.pasto;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PastoRepository extends JpaRepository<Pasto, Long> {
    List<Pasto> findByFazendaId(Long fazendaId);
    Optional<Pasto> findByLoteAlocadoId(Long loteId);
}
