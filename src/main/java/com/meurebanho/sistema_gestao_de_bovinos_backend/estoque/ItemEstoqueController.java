package com.meurebanho.sistema_gestao_de_bovinos_backend.estoque;

import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.estoque.ItemEstoqueRequestDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.estoque.ItemEstoqueResponseDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.estoque.MovimentacaoEstoqueRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estoque")
public class ItemEstoqueController {

    private final ItemEstoqueService itemEstoqueService;

    public ItemEstoqueController(ItemEstoqueService itemEstoqueService) {
        this.itemEstoqueService = itemEstoqueService;
    }

    @PostMapping
    public ResponseEntity<ItemEstoqueResponseDTO> criarItemEstoque(@RequestBody @Valid ItemEstoqueRequestDTO dto) {
        ItemEstoqueResponseDTO novoItem = itemEstoqueService.criarItemEstoque(dto);
        return ResponseEntity.ok(novoItem);
    }

    @GetMapping
    public ResponseEntity<List<ItemEstoqueResponseDTO>> listarItensEstoque() {
        List<ItemEstoqueResponseDTO> itens = itemEstoqueService.listarItensEstoque();
        return ResponseEntity.ok(itens);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemEstoqueResponseDTO> atualizarItemEstoque(@PathVariable Long id, @RequestBody @Valid ItemEstoqueRequestDTO dto) {
        ItemEstoqueResponseDTO itemAtualizado = itemEstoqueService.atualizarItemEstoque(id, dto);
        return ResponseEntity.ok(itemAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarItemEstoque(@PathVariable Long id) {
        itemEstoqueService.deletarItemEstoque(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/entrada")
    public ResponseEntity<Void> registrarEntrada(@PathVariable Long id, @RequestBody @Valid MovimentacaoEstoqueRequestDTO dto) {
        itemEstoqueService.registrarEntrada(id, dto.quantidade());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/saida")
    public ResponseEntity<Void> registrarSaida(@PathVariable Long id, @RequestBody @Valid MovimentacaoEstoqueRequestDTO dto) {
        itemEstoqueService.registrarSaida(id, dto.quantidade());
        return ResponseEntity.ok().build();
    }
}
