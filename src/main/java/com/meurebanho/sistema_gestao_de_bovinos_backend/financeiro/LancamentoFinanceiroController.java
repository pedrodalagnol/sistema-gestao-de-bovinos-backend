package com.meurebanho.sistema_gestao_de_bovinos_backend.financeiro;

import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.financeiro.LancamentoFinanceiroRequestDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.financeiro.LancamentoFinanceiroResponseDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.financeiro.RelatorioDRE_DTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.financeiro.RelatorioFluxoCaixaDTO;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/financeiro")
public class LancamentoFinanceiroController {

    private final LancamentoFinanceiroService lancamentoService;

    public LancamentoFinanceiroController(LancamentoFinanceiroService lancamentoService) {
        this.lancamentoService = lancamentoService;
    }

    @PostMapping("/lancamentos")
    public ResponseEntity<LancamentoFinanceiroResponseDTO> criarLancamento(@RequestBody @Valid LancamentoFinanceiroRequestDTO dto) {
        LancamentoFinanceiroResponseDTO novoLancamento = lancamentoService.criarLancamento(dto);
        return ResponseEntity.ok(novoLancamento);
    }

    @GetMapping("/lancamentos")
    public ResponseEntity<List<LancamentoFinanceiroResponseDTO>> listarLancamentos() {
        List<LancamentoFinanceiroResponseDTO> lancamentos = lancamentoService.listarLancamentos();
        return ResponseEntity.ok(lancamentos);
    }

    @PutMapping("/lancamentos/{id}")
    public ResponseEntity<LancamentoFinanceiroResponseDTO> atualizarLancamento(@PathVariable Long id, @RequestBody @Valid LancamentoFinanceiroRequestDTO dto) {
        LancamentoFinanceiroResponseDTO lancamentoAtualizado = lancamentoService.atualizarLancamento(id, dto);
        return ResponseEntity.ok(lancamentoAtualizado);
    }

    @DeleteMapping("/lancamentos/{id}")
    public ResponseEntity<Void> deletarLancamento(@PathVariable Long id) {
        lancamentoService.deletarLancamento(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/relatorios/fluxo-caixa")
    public ResponseEntity<RelatorioFluxoCaixaDTO> getFluxoDeCaixa(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        RelatorioFluxoCaixaDTO relatorio = lancamentoService.gerarFluxoDeCaixa(dataInicio, dataFim);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/relatorios/dre")
    public ResponseEntity<RelatorioDRE_DTO> getDRE(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        RelatorioDRE_DTO relatorio = lancamentoService.gerarDRE(dataInicio, dataFim);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/relatorios/custo-arroba/{loteId}")
    public ResponseEntity<BigDecimal> getCustoPorArroba(@PathVariable Long loteId) {
        BigDecimal custo = lancamentoService.calcularCustoPorArroba(loteId);
        return ResponseEntity.ok(custo);
    }
}
