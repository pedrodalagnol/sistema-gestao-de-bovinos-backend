package com.meurebanho.sistema_gestao_de_bovinos_backend.lote;

import com.meurebanho.sistema_gestao_de_bovinos_backend.animal.Animal;
import com.meurebanho.sistema_gestao_de_bovinos_backend.animal.AnimalRepository;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.lote.AssignAnimalsRequestDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.lote.LoteRequestDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.lote.LoteResponseDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.usuario.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoteService {

    private final LoteRepository loteRepository;
    private final AnimalRepository animalRepository;

    private Usuario getAuthenticatedUser() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private LoteResponseDTO mapToResponseDTO(Lote lote) {
        return new LoteResponseDTO(lote.getId(), lote.getNome(), lote.getDescricao());
    }

    @Transactional
    public LoteResponseDTO createLote(LoteRequestDTO requestDTO) {
        Usuario usuarioLogado = getAuthenticatedUser();
        Lote lote = new Lote();
        lote.setNome(requestDTO.nome());
        lote.setDescricao(requestDTO.descricao());
        lote.setFazenda(usuarioLogado.getFazenda()); // Link com a fazenda do usuário

        Lote savedLote = loteRepository.save(lote);
        return mapToResponseDTO(savedLote);
    }

    public List<LoteResponseDTO> getLotesByFazenda() {
        Usuario usuarioLogado = getAuthenticatedUser();
        return loteRepository.findByFazendaId(usuarioLogado.getFazenda().getId()).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public LoteResponseDTO updateLote(Long loteId, LoteRequestDTO requestDTO) {
        Usuario usuarioLogado = getAuthenticatedUser();
        Lote lote = loteRepository.findByIdAndFazendaId(loteId, usuarioLogado.getFazenda().getId())
                .orElseThrow(() -> new RuntimeException("Lote não encontrado ou não pertence à sua fazenda."));

        lote.setNome(requestDTO.nome());
        lote.setDescricao(requestDTO.descricao());
        Lote updatedLote = loteRepository.save(lote);
        return mapToResponseDTO(updatedLote);
    }

    @Transactional
    public void deleteLote(Long loteId) {
        Usuario usuarioLogado = getAuthenticatedUser();
        Lote lote = loteRepository.findByIdAndFazendaId(loteId, usuarioLogado.getFazenda().getId())
                .orElseThrow(() -> new RuntimeException("Lote não encontrado ou não pertence à sua fazenda."));

        // Antes de deletar o lote, remove a associação dos animais com ele
        List<Animal> animaisNoLote = lote.getAnimais();
        for (Animal animal : animaisNoLote) {
            animal.setLote(null);
        }
        animalRepository.saveAll(animaisNoLote);

        loteRepository.delete(lote);
    }

    @Transactional
    public void assignAnimalsToLot(Long loteId, AssignAnimalsRequestDTO request) {
        Usuario usuarioLogado = getAuthenticatedUser();
        Long fazendaId = usuarioLogado.getFazenda().getId();

        // 1. Valida se o lote de destino pertence à fazenda do usuário
        Lote lote = loteRepository.findByIdAndFazendaId(loteId, fazendaId)
                .orElseThrow(() -> new RuntimeException("Lote de destino não encontrado."));

        // 2. Busca todos os animais a serem movidos
        List<Animal> animais = animalRepository.findAllById(request.animalIds());

        // 3. Validação de segurança: Garante que TODOS os animais pertencem à fazenda do usuário
        for (Animal animal : animais) {
            if (!animal.getFazenda().getId().equals(fazendaId)) {
                throw new SecurityException("Tentativa de mover animal que não pertence à sua fazenda.");
            }
            animal.setLote(lote); // Atribui o lote ao animal
        }

        // 4. Salva todos os animais atualizados
        animalRepository.saveAll(animais);
    }
}