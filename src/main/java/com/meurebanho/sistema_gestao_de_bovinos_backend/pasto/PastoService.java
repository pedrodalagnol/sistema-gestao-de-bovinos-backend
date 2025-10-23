package com.meurebanho.sistema_gestao_de_bovinos_backend.pasto;

import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.pasto.PastoRequestDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.pasto.PastoResponseDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.lote.Lote;
import com.meurebanho.sistema_gestao_de_bovinos_backend.lote.LoteRepository;
import com.meurebanho.sistema_gestao_de_bovinos_backend.usuario.Fazenda;
import com.meurebanho.sistema_gestao_de_bovinos_backend.usuario.Usuario;
import com.meurebanho.sistema_gestao_de_bovinos_backend.usuario.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PastoService {

    private final PastoRepository pastoRepository;
    private final LoteRepository loteRepository;
    private final UsuarioRepository usuarioRepository;

    public PastoService(PastoRepository pastoRepository, LoteRepository loteRepository, UsuarioRepository usuarioRepository) {
        this.pastoRepository = pastoRepository;
        this.loteRepository = loteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public PastoResponseDTO criarPasto(PastoRequestDTO dto) {
        Fazenda fazenda = getFazendaFromContext();
        Pasto pasto = new Pasto();
        pasto.setNome(dto.nome());
        pasto.setAreaHa(dto.areaHa());
        pasto.setStatus(PastoStatus.EM_DESCANSO); // Status inicial
        pasto.setDataInicioStatus(LocalDate.now());
        pasto.setFazenda(fazenda);
        Pasto savedPasto = pastoRepository.save(pasto);
        return convertToResponseDTO(savedPasto);
    }

    @Transactional(readOnly = true)
    public List<PastoResponseDTO> listarPastos() {
        Fazenda fazenda = getFazendaFromContext();
        return pastoRepository.findByFazendaId(fazenda.getId()).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PastoResponseDTO atualizarPasto(Long id, PastoRequestDTO dto) {
        Fazenda fazenda = getFazendaFromContext();
        Pasto pasto = pastoRepository.findById(id)
                .filter(p -> p.getFazenda().getId().equals(fazenda.getId()))
                .orElseThrow(() -> new RuntimeException("Pasto não encontrado ou não pertence à fazenda."));

        pasto.setNome(dto.nome());
        pasto.setAreaHa(dto.areaHa());
        Pasto updatedPasto = pastoRepository.save(pasto);
        return convertToResponseDTO(updatedPasto);
    }

    @Transactional
    public void deletarPasto(Long id) {
        Fazenda fazenda = getFazendaFromContext();
        Pasto pasto = pastoRepository.findById(id)
                .filter(p -> p.getFazenda().getId().equals(fazenda.getId()))
                .orElseThrow(() -> new RuntimeException("Pasto não encontrado ou não pertence à fazenda."));

        if (pasto.getLoteAlocado() != null) {
            throw new IllegalStateException("Não é possível excluir um pasto com um lote alocado.");
        }

        pastoRepository.delete(pasto);
    }

    @Transactional
    public void alocarLote(Long pastoId, Long loteId) {
        Fazenda fazenda = getFazendaFromContext();
        Pasto pasto = pastoRepository.findById(pastoId)
                .filter(p -> p.getFazenda().getId().equals(fazenda.getId()))
                .orElseThrow(() -> new RuntimeException("Pasto não encontrado ou não pertence à fazenda."));

        Lote lote = loteRepository.findById(loteId)
                .filter(l -> l.getFazenda().getId().equals(fazenda.getId()))
                .orElseThrow(() -> new RuntimeException("Lote não encontrado ou não pertence à fazenda."));

        // Verifica se o lote já está alocado em outro pasto
        pastoRepository.findByLoteAlocadoId(loteId).ifPresent(p -> {
            if (!p.getId().equals(pastoId)) {
                throw new IllegalStateException("Este lote já está alocado no pasto '" + p.getNome() + "'.");
            }
        });

        pasto.setLoteAlocado(lote);
        pasto.setStatus(PastoStatus.EM_USO);
        pasto.setDataInicioStatus(LocalDate.now());
        pastoRepository.save(pasto);
    }

    @Transactional
    public void liberarPasto(Long pastoId, PastoStatus novoStatus) {
        if (novoStatus == PastoStatus.EM_USO) {
            throw new IllegalArgumentException("Para liberar um pasto, o novo status deve ser EM_DESCANSO ou VEDADO.");
        }

        Fazenda fazenda = getFazendaFromContext();
        Pasto pasto = pastoRepository.findById(pastoId)
                .filter(p -> p.getFazenda().getId().equals(fazenda.getId()))
                .orElseThrow(() -> new RuntimeException("Pasto não encontrado ou não pertence à fazenda."));

        pasto.setLoteAlocado(null);
        pasto.setStatus(novoStatus);
        pasto.setDataInicioStatus(LocalDate.now());
        pastoRepository.save(pasto);
    }

    private Fazenda getFazendaFromContext() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        return usuario.getFazenda();
    }

    private PastoResponseDTO convertToResponseDTO(Pasto pasto) {
        Double taxaOcupacao = calcularTaxaOcupacao(pasto);
        com.meurebanho.sistema_gestao_de_bovinos_backend.dto.lote.LoteResponseDTO loteDto = null;
        if (pasto.getLoteAlocado() != null) {
            loteDto = new com.meurebanho.sistema_gestao_de_bovinos_backend.dto.lote.LoteResponseDTO(
                    pasto.getLoteAlocado().getId(), 
                    pasto.getLoteAlocado().getNome(), 
                    pasto.getLoteAlocado().getDescricao(),
                    pasto.getNome() // O lote está neste pasto
            );
        }

        return new PastoResponseDTO(
                pasto.getId(),
                pasto.getNome(),
                pasto.getAreaHa(),
                pasto.getStatus(),
                pasto.getDataInicioStatus(),
                loteDto,
                taxaOcupacao
        );
    }

    private Double calcularTaxaOcupacao(Pasto pasto) {
        if (pasto.getLoteAlocado() == null || pasto.getAreaHa() == null || pasto.getAreaHa() == 0) {
            return 0.0;
        }
        int numeroDeAnimais = pasto.getLoteAlocado().getAnimais() != null ? pasto.getLoteAlocado().getAnimais().size() : 0;
        return numeroDeAnimais / pasto.getAreaHa();
    }
}
