package com.meurebanho.sistema_gestao_de_bovinos_backend.lote;

import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.lote.AssignAnimalsRequestDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.lote.LoteDetailsResponseDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.lote.LoteRequestDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.lote.LoteResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lotes")
@RequiredArgsConstructor
public class LoteController {

    private final LoteService loteService;

    @PostMapping
    public ResponseEntity<LoteResponseDTO> createLote(@RequestBody LoteRequestDTO requestDTO) {
        return new ResponseEntity<>(loteService.createLote(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<LoteResponseDTO>> getMeusLotes() {
        return ResponseEntity.ok(loteService.getLotesByFazenda());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoteDetailsResponseDTO> getLoteDetails(@PathVariable Long id) {
        return ResponseEntity.ok(loteService.getLoteDetails(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoteResponseDTO> updateLote(@PathVariable Long id, @RequestBody LoteRequestDTO requestDTO) {
        return ResponseEntity.ok(loteService.updateLote(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLote(@PathVariable Long id) {
        loteService.deleteLote(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{loteId}/atribuir-animais")
    public ResponseEntity<Void> assignAnimals(@PathVariable Long loteId, @RequestBody AssignAnimalsRequestDTO request) {
        loteService.assignAnimalsToLot(loteId, request);
        return ResponseEntity.ok().build();
    }
}