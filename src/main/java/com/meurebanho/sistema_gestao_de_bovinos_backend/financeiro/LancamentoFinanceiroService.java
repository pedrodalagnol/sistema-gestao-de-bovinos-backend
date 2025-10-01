package com.meurebanho.sistema_gestao_de_bovinos_backend.financeiro;

import com.meurebanho.sistema_gestao_de_bovinos_backend.animal.Animal;
import com.meurebanho.sistema_gestao_de_bovinos_backend.animal.EventoAnimal;
import com.meurebanho.sistema_gestao_de_bovinos_backend.animal.EventoAnimalRepository;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.financeiro.LancamentoFinanceiroRequestDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.financeiro.LancamentoFinanceiroResponseDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.financeiro.RelatorioDRE_DTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.financeiro.RelatorioFluxoCaixaDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.lote.Lote;
import com.meurebanho.sistema_gestao_de_bovinos_backend.lote.LoteRepository;
import com.meurebanho.sistema_gestao_de_bovinos_backend.usuario.Fazenda;
import com.meurebanho.sistema_gestao_de_bovinos_backend.usuario.Usuario;
import com.meurebanho.sistema_gestao_de_bovinos_backend.usuario.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class LancamentoFinanceiroService {

    private final LancamentoFinanceiroRepository lancamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LoteRepository loteRepository;
    private final EventoAnimalRepository eventoAnimalRepository;
    private static final BigDecimal ARROBA_EM_KG = new BigDecimal("30");

    public LancamentoFinanceiroService(LancamentoFinanceiroRepository lancamentoRepository, UsuarioRepository usuarioRepository, LoteRepository loteRepository, EventoAnimalRepository eventoAnimalRepository) {
        this.lancamentoRepository = lancamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.loteRepository = loteRepository;
        this.eventoAnimalRepository = eventoAnimalRepository;
    }

    @Transactional
    public LancamentoFinanceiroResponseDTO criarLancamento(LancamentoFinanceiroRequestDTO dto) {
        Fazenda fazenda = getFazendaFromContext();
        LancamentoFinanceiro lancamento = new LancamentoFinanceiro();
        lancamento.setDescricao(dto.descricao());
        lancamento.setValor(dto.valor());
        lancamento.setTipo(dto.tipo());
        lancamento.setDataLancamento(dto.dataLancamento());
        lancamento.setCategoria(dto.categoria());
        lancamento.setFazenda(fazenda);

        if (dto.loteId() != null) {
            Lote lote = loteRepository.findById(dto.loteId())
                    .filter(l -> l.getFazenda().getId().equals(fazenda.getId()))
                    .orElseThrow(() -> new RuntimeException("Lote não encontrado ou não pertence à fazenda."));
            lancamento.setLote(lote);
        }

        LancamentoFinanceiro savedLancamento = lancamentoRepository.save(lancamento);
        return convertToResponseDTO(savedLancamento);
    }

    @Transactional(readOnly = true)
    public List<LancamentoFinanceiroResponseDTO> listarLancamentos() {
        Fazenda fazenda = getFazendaFromContext();
        return lancamentoRepository.findByFazendaId(fazenda.getId()).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public LancamentoFinanceiroResponseDTO atualizarLancamento(Long id, LancamentoFinanceiroRequestDTO dto) {
        Fazenda fazenda = getFazendaFromContext();
        LancamentoFinanceiro lancamento = lancamentoRepository.findById(id)
                .filter(l -> l.getFazenda().getId().equals(fazenda.getId()))
                .orElseThrow(() -> new RuntimeException("Lançamento não encontrado ou não pertence à fazenda."));

        lancamento.setDescricao(dto.descricao());
        lancamento.setValor(dto.valor());
        lancamento.setTipo(dto.tipo());
        lancamento.setDataLancamento(dto.dataLancamento());
        lancamento.setCategoria(dto.categoria());

        if (dto.loteId() != null) {
            Lote lote = loteRepository.findById(dto.loteId())
                    .filter(l -> l.getFazenda().getId().equals(fazenda.getId()))
                    .orElseThrow(() -> new RuntimeException("Lote não encontrado ou não pertence à fazenda."));
            lancamento.setLote(lote);
        } else {
            lancamento.setLote(null);
        }

        LancamentoFinanceiro updatedLancamento = lancamentoRepository.save(lancamento);
        return convertToResponseDTO(updatedLancamento);
    }

    @Transactional
    public void deletarLancamento(Long id) {
        Fazenda fazenda = getFazendaFromContext();
        LancamentoFinanceiro lancamento = lancamentoRepository.findById(id)
                .filter(l -> l.getFazenda().getId().equals(fazenda.getId()))
                .orElseThrow(() -> new RuntimeException("Lançamento não encontrado ou não pertence à fazenda."));
        lancamentoRepository.delete(lancamento);
    }

    @Transactional(readOnly = true)
    public RelatorioFluxoCaixaDTO gerarFluxoDeCaixa(LocalDate dataInicio, LocalDate dataFim) {
        Fazenda fazenda = getFazendaFromContext();
        List<LancamentoFinanceiro> lancamentos = lancamentoRepository.findByFazendaIdAndDataLancamentoBetween(fazenda.getId(), dataInicio, dataFim);

        BigDecimal totalReceitas = lancamentos.stream()
                .filter(l -> l.getTipo() == TipoLancamento.RECEITA)
                .map(LancamentoFinanceiro::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDespesas = lancamentos.stream()
                .filter(l -> l.getTipo() == TipoLancamento.DESPESA)
                .map(LancamentoFinanceiro::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoFinal = totalReceitas.subtract(totalDespesas);

        return new RelatorioFluxoCaixaDTO(totalReceitas, totalDespesas, saldoFinal);
    }

    @Transactional(readOnly = true)
    public RelatorioDRE_DTO gerarDRE(LocalDate dataInicio, LocalDate dataFim) {
        Fazenda fazenda = getFazendaFromContext();
        List<LancamentoFinanceiro> lancamentos = lancamentoRepository.findByFazendaIdAndDataLancamentoBetween(fazenda.getId(), dataInicio, dataFim);

        BigDecimal totalReceitas = lancamentos.stream()
                .filter(l -> l.getTipo() == TipoLancamento.RECEITA)
                .map(LancamentoFinanceiro::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> despesasPorCategoria = lancamentos.stream()
                .filter(l -> l.getTipo() == TipoLancamento.DESPESA)
                .collect(Collectors.groupingBy(
                        l -> l.getCategoria() != null ? l.getCategoria() : "Sem Categoria",
                        Collectors.reducing(BigDecimal.ZERO, LancamentoFinanceiro::getValor, BigDecimal::add)
                ));

        BigDecimal totalDespesas = despesasPorCategoria.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal resultadoLiquido = totalReceitas.subtract(totalDespesas);

        return new RelatorioDRE_DTO(totalReceitas, despesasPorCategoria, totalDespesas, resultadoLiquido);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularCustoPorArroba(Long loteId) {
        Fazenda fazenda = getFazendaFromContext();

        List<LancamentoFinanceiro> despesasDoLote = lancamentoRepository.findDespesasByLoteIdAndFazendaId(loteId, fazenda.getId());
        BigDecimal custoTotal = despesasDoLote.stream()
                .map(LancamentoFinanceiro::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Lote lote = loteRepository.findById(loteId)
                .filter(l -> l.getFazenda().getId().equals(fazenda.getId()))
                .orElseThrow(() -> new RuntimeException("Lote não encontrado ou não pertence à fazenda."));

        AtomicReference<BigDecimal> pesoTotalKg = new AtomicReference<>(BigDecimal.ZERO);
        for (Animal animal : lote.getAnimais()) {
            eventoAnimalRepository.findFirstByAnimalIdAndTipoEventoOrderByDataEventoDesc(animal.getId(), EventoAnimal.TipoEvento.VENDA)
                    .ifPresent(eventoVenda -> {
                        if (eventoVenda.getValor() != null) {
                            pesoTotalKg.set(pesoTotalKg.get().add(BigDecimal.valueOf(eventoVenda.getValor())));
                        }
                    });
        }

        if (pesoTotalKg.get().compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalStateException("Nenhum animal vendido com peso registrado encontrado para este lote.");
        }

        BigDecimal totalArrobas = pesoTotalKg.get().divide(ARROBA_EM_KG, 2, RoundingMode.HALF_UP);

        if (totalArrobas.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return custoTotal.divide(totalArrobas, 2, RoundingMode.HALF_UP);
    }

    private Fazenda getFazendaFromContext() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        return usuario.getFazenda();
    }

    private LancamentoFinanceiroResponseDTO convertToResponseDTO(LancamentoFinanceiro lancamento) {
        return new LancamentoFinanceiroResponseDTO(
                lancamento.getId(),
                lancamento.getDescricao(),
                lancamento.getValor(),
                lancamento.getTipo(),
                lancamento.getDataLancamento(),
                lancamento.getCategoria(),
                lancamento.getLote() != null ? lancamento.getLote().getId() : null
        );
    }
}
