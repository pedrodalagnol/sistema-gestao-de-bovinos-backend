package com.meurebanho.sistema_gestao_de_bovinos_backend.lote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {

    /**
     * Encontra todos os lotes pertencentes a uma fazenda específica.
     * @param fazendaId O ID da fazenda.
     * @return Uma lista de lotes.
     */
    List<Lote> findByFazendaId(Long fazendaId);

    /**
     * Encontra um lote pelo seu ID e pelo ID da fazenda, garantindo que o usuário
     * só possa acessar lotes de sua própria fazenda.
     * @param id O ID do lote.
     * @param fazendaId O ID da fazenda.
     * @return Um Optional contendo o lote, se encontrado.
     */
    Optional<Lote> findByIdAndFazendaId(Long id, Long fazendaId);
}