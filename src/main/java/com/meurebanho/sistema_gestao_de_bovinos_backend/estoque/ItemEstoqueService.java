package com.meurebanho.sistema_gestao_de_bovinos_backend.estoque;

import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.estoque.ItemEstoqueRequestDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.estoque.ItemEstoqueResponseDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.usuario.Fazenda;
import com.meurebanho.sistema_gestao_de_bovinos_backend.usuario.Usuario;
import com.meurebanho.sistema_gestao_de_bovinos_backend.usuario.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemEstoqueService {

    private final ItemEstoqueRepository itemEstoqueRepository;
    private final UsuarioRepository usuarioRepository;

    public ItemEstoqueService(ItemEstoqueRepository itemEstoqueRepository, UsuarioRepository usuarioRepository) {
        this.itemEstoqueRepository = itemEstoqueRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public ItemEstoqueResponseDTO criarItemEstoque(ItemEstoqueRequestDTO dto) {
        Fazenda fazenda = getFazendaFromContext();
        ItemEstoque item = new ItemEstoque();
        item.setNome(dto.nome());
        item.setCategoria(dto.categoria());
        item.setUnidadeMedida(dto.unidadeMedida());
        item.setQuantidadeAtual(dto.quantidadeInicial());
        item.setFazenda(fazenda);
        ItemEstoque savedItem = itemEstoqueRepository.save(item);
        return convertToResponseDTO(savedItem);
    }

    @Transactional(readOnly = true)
    public List<ItemEstoqueResponseDTO> listarItensEstoque() {
        Fazenda fazenda = getFazendaFromContext();
        return itemEstoqueRepository.findByFazendaId(fazenda.getId()).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ItemEstoqueResponseDTO atualizarItemEstoque(Long id, ItemEstoqueRequestDTO dto) {
        Fazenda fazenda = getFazendaFromContext();
        ItemEstoque item = itemEstoqueRepository.findById(id)
                .filter(i -> i.getFazenda().getId().equals(fazenda.getId()))
                .orElseThrow(() -> new RuntimeException("Item de estoque não encontrado ou não pertence à fazenda."));

        item.setNome(dto.nome());
        item.setCategoria(dto.categoria());
        item.setUnidadeMedida(dto.unidadeMedida());
        // A quantidade atual não é alterada aqui, apenas na entrada/saída
        ItemEstoque updatedItem = itemEstoqueRepository.save(item);
        return convertToResponseDTO(updatedItem);
    }

    @Transactional
    public void deletarItemEstoque(Long id) {
        Fazenda fazenda = getFazendaFromContext();
        ItemEstoque item = itemEstoqueRepository.findById(id)
                .filter(i -> i.getFazenda().getId().equals(fazenda.getId()))
                .orElseThrow(() -> new RuntimeException("Item de estoque não encontrado ou não pertence à fazenda."));
        itemEstoqueRepository.delete(item);
    }

    @Transactional
    public void registrarEntrada(Long id, Double quantidade) {
        Fazenda fazenda = getFazendaFromContext();
        ItemEstoque item = itemEstoqueRepository.findById(id)
                .filter(i -> i.getFazenda().getId().equals(fazenda.getId()))
                .orElseThrow(() -> new RuntimeException("Item de estoque não encontrado ou não pertence à fazenda."));

        item.setQuantidadeAtual(item.getQuantidadeAtual() + quantidade);
        itemEstoqueRepository.save(item);
    }

    @Transactional
    public void registrarSaida(Long id, Double quantidade) {
        Fazenda fazenda = getFazendaFromContext();
        ItemEstoque item = itemEstoqueRepository.findById(id)
                .filter(i -> i.getFazenda().getId().equals(fazenda.getId()))
                .orElseThrow(() -> new RuntimeException("Item de estoque não encontrado ou não pertence à fazenda."));

        if (item.getQuantidadeAtual() < quantidade) {
            throw new IllegalStateException("Quantidade em estoque insuficiente para a saída.");
        }

        item.setQuantidadeAtual(item.getQuantidadeAtual() - quantidade);
        itemEstoqueRepository.save(item);
    }

    private Fazenda getFazendaFromContext() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        return usuario.getFazenda();
    }

    private ItemEstoqueResponseDTO convertToResponseDTO(ItemEstoque item) {
        return new ItemEstoqueResponseDTO(
                item.getId(),
                item.getNome(),
                item.getCategoria(),
                item.getUnidadeMedida(),
                item.getQuantidadeAtual()
        );
    }
}
