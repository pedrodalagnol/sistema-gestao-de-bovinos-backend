package com.meurebanho.sistema_gestao_de_bovinos_backend.financeiro;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LancamentoFinanceiroRepository extends JpaRepository<LancamentoFinanceiro, Long> {

    List<LancamentoFinanceiro> findByFazendaIdAndDataLancamentoBetween(
            Long fazendaId, LocalDate dataInicio, LocalDate dataFim);

    List<LancamentoFinanceiro> findByFazendaId(Long fazendaId);

    @Query("SELECT l FROM LancamentoFinanceiro l WHERE l.lote.id = :loteId AND l.tipo = 'DESPESA' AND l.fazenda.id = :fazendaId")
    List<LancamentoFinanceiro> findDespesasByLoteIdAndFazendaId(
            @Param("loteId") Long loteId, @Param("fazendaId") Long fazendaId);

}
