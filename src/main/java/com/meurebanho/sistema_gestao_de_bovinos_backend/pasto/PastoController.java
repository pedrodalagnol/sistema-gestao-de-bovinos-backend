package com.meurebanho.sistema_gestao_de_bovinos_backend.pasto;

import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.pasto.PastoRequestDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.pasto.PastoResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pastos")
public class PastoController {

    private final PastoService pastoService;

    public PastoController(PastoService pastoService) {
        this.pastoService = pastoService;
    }

    @PostMapping
    public ResponseEntity<PastoResponseDTO> criarPasto(@RequestBody @Valid PastoRequestDTO dto) {
        PastoResponseDTO novoPasto = pastoService.criarPasto(dto);
        return ResponseEntity.ok(novoPasto);
    }

    @GetMapping
    public ResponseEntity<List<PastoResponseDTO>> listarPastos() {
        List<PastoResponseDTO> pastos = pastoService.listarPastos();
        return ResponseEntity.ok(pastos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PastoResponseDTO> atualizarPasto(@PathVariable Long id, @RequestBody @Valid PastoRequestDTO dto) {
        PastoResponseDTO pastoAtualizado = pastoService.atualizarPasto(id, dto);
        return ResponseEntity.ok(pastoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPasto(@PathVariable Long id) {
        pastoService.deletarPasto(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{pastoId}/alocar/{loteId}")
    public ResponseEntity<Void> alocarLote(@PathVariable Long pastoId, @PathVariable Long loteId) {
        pastoService.alocarLote(pastoId, loteId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{pastoId}/liberar")
    public ResponseEntity<Void> liberarPasto(@PathVariable Long pastoId, @RequestParam("novoStatus") PastoStatus novoStatus) {
        pastoService.liberarPasto(pastoId, novoStatus);
        return ResponseEntity.ok().build();
    }
}
