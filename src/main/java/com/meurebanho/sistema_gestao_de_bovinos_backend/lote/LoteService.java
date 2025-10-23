package com.meurebanho.sistema_gestao_de_bovinos_backend.lote;

import com.meurebanho.sistema_gestao_de_bovinos_backend.animal.Animal;
import com.meurebanho.sistema_gestao_de_bovinos_backend.animal.AnimalRepository;
import com.meurebanho.sistema_gestao_de_bovinos_backend.animal.AnimalService;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.animal.AnimalResponseDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.lote.AssignAnimalsRequestDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.lote.LoteDetailsResponseDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.lote.LoteRequestDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.lote.LoteResponseDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.pasto.Pasto;
import com.meurebanho.sistema_gestao_de_bovinos_backend.pasto.PastoRepository;
import com.meurebanho.sistema_gestao_de_bovinos_backend.usuario.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoteService {

    private final LoteRepository loteRepository;
    private final AnimalRepository animalRepository;
    private final PastoRepository pastoRepository;
    private final AnimalService animalService; // Injetado

    private Usuario getAuthenticatedUser() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private LoteResponseDTO mapToResponseDTO(Lote lote) {
        Optional<Pasto> pastoOptional = pastoRepository.findByLoteAlocadoId(lote.getId());
        String pastoAtualNome = pastoOptional.map(Pasto::getNome).orElse(null);
        return new LoteResponseDTO(lote.getId(), lote.getNome(), lote.getDescricao(), pastoAtualNome);
    }

    @Transactional
    public LoteResponseDTO createLote(LoteRequestDTO requestDTO) {
        Usuario usuarioLogado = getAuthenticatedUser();
        Lote lote = new Lote();
        lote.setNome(requestDTO.nome());
        lote.setDescricao(requestDTO.descricao());
        lote.setFazenda(usuarioLogado.getFazenda());

        Lote savedLote = loteRepository.save(lote);
        return mapToResponseDTO(savedLote);
    }

    public List<LoteResponseDTO> getLotesByFazenda() {
        Usuario usuarioLogado = getAuthenticatedUser();
        return loteRepository.findByFazendaId(usuarioLogado.getFazenda().getId()).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LoteDetailsResponseDTO getLoteDetails(Long loteId) {
        Usuario usuarioLogado = getAuthenticatedUser();
        Lote lote = loteRepository.findByIdAndFazendaId(loteId, usuarioLogado.getFazenda().getId())
                .orElseThrow(() -> new RuntimeException("Lote não encontrado ou não pertence à sua fazenda."));

        List<AnimalResponseDTO> animalDTOs = lote.getAnimais().stream()
                .map(animalService::mapToResponseDTO) // Reutiliza o mapper do AnimalService
                .collect(Collectors.toList());

        Optional<Pasto> pastoOptional = pastoRepository.findByLoteAlocadoId(lote.getId());
        String pastoAtualNome = pastoOptional.map(Pasto::getNome).orElse(null);

        return new LoteDetailsResponseDTO(lote.getId(), lote.getNome(), lote.getDescricao(), pastoAtualNome, animalDTOs);
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

        Lote lote = loteRepository.findByIdAndFazendaId(loteId, fazendaId)
                .orElseThrow(() -> new RuntimeException("Lote de destino não encontrado."));

        List<Animal> animais = animalRepository.findAllById(request.animalIds());

        for (Animal animal : animais) {
            if (!animal.getFazenda().getId().equals(fazendaId)) {
                throw new SecurityException("Tentativa de mover animal que não pertence à sua fazenda.");
            }
            animal.setLote(lote);
        }

        animalRepository.saveAll(animais);
    }
}