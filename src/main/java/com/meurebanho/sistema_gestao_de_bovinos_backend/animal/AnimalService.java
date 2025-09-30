package com.meurebanho.sistema_gestao_de_bovinos_backend.animal;

import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.animal.AnimalDetailsResponseDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.animal.AnimalRequestDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.animal.AnimalResponseDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.animal.EventoRequestDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.usuario.Fazenda;
import com.meurebanho.sistema_gestao_de_bovinos_backend.usuario.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final EventoAnimalRepository eventoAnimalRepository;

    @Transactional
    public AnimalResponseDTO createAnimal(AnimalRequestDTO requestDTO) {
        Usuario usuarioLogado = getAuthenticatedUser();

        Animal animal = new Animal();
        animal.setFazenda(usuarioLogado.getFazenda()); // VINCULA O ANIMAL À FAZENDA DO USUÁRIO LOGADO
        animal.setIdentificador(requestDTO.identificador());
        animal.setRaca(requestDTO.raca());
        animal.setSexo(requestDTO.sexo());
        animal.setDataNascimento(requestDTO.dataNascimento());

        Animal savedAnimal = animalRepository.save(animal);
        return mapToResponseDTO(savedAnimal);
    }

    public List<AnimalResponseDTO> getAnimaisByFazenda() {
        Usuario usuarioLogado = getAuthenticatedUser();
        Long fazendaId = usuarioLogado.getFazenda().getId();

        return animalRepository.findByFazendaId(fazendaId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Método helper para pegar o usuário do contexto de segurança
    private Usuario getAuthenticatedUser() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // Método helper para mapear a Entidade para o DTO de resposta
    private AnimalResponseDTO mapToResponseDTO(Animal animal) {
        return new AnimalResponseDTO(
                animal.getId(),
                animal.getIdentificador(),
                animal.getSexo(),
                animal.getRaca(),
                animal.getDataNascimento(),
                animal.getStatus()
        );
    }

    @Transactional
    public AnimalResponseDTO updateAnimal(Long animalId, AnimalRequestDTO requestDTO) {
        Usuario usuarioLogado = getAuthenticatedUser();
        Long fazendaId = usuarioLogado.getFazenda().getId();

        // Busca o animal garantindo que ele pertence à fazenda do usuário
        Animal animal = animalRepository.findByIdAndFazendaId(animalId, fazendaId)
                .orElseThrow(() -> new RuntimeException("Animal não encontrado ou não pertence à sua fazenda."));

        // Atualiza os dados
        animal.setIdentificador(requestDTO.identificador());
        animal.setRaca(requestDTO.raca());
        animal.setSexo(requestDTO.sexo());
        animal.setDataNascimento(requestDTO.dataNascimento());

        Animal updatedAnimal = animalRepository.save(animal);
        return mapToResponseDTO(updatedAnimal);
    }

    @Transactional
    public void deleteAnimal(Long animalId) {
        Usuario usuarioLogado = getAuthenticatedUser();
        Long fazendaId = usuarioLogado.getFazenda().getId();

        // Busca o animal para garantir a permissão antes de deletar
        Animal animal = animalRepository.findByIdAndFazendaId(animalId, fazendaId)
                .orElseThrow(() -> new RuntimeException("Animal não encontrado ou não pertence à sua fazenda."));

        // Deleta o animal
        animalRepository.delete(animal);
    }

    //Método para buscar os detalhes completos de um animal
    public AnimalDetailsResponseDTO getAnimalDetails(Long animalId) {
        Usuario usuarioLogado = getAuthenticatedUser();
        Long fazendaId = usuarioLogado.getFazenda().getId();

        Animal animal = animalRepository.findByIdAndFazendaId(animalId, fazendaId)
                .orElseThrow(() -> new RuntimeException("Animal não encontrado."));

        List<EventoAnimal> historico = eventoAnimalRepository.findByAnimalIdOrderByDataEventoAsc(animalId);
        double gmd = calcularGMD(historico);

        return new AnimalDetailsResponseDTO(
                animal.getId(),
                animal.getIdentificador(),
                animal.getSexo(),
                animal.getRaca(),
                animal.getDataNascimento(),
                animal.getStatus(),
                gmd,
                historico
        );
    }

    //Método para adicionar um evento a um animal
    @Transactional
    public EventoAnimal addEvento(Long animalId, EventoRequestDTO requestDTO) {
        Usuario usuarioLogado = getAuthenticatedUser();
        Long fazendaId = usuarioLogado.getFazenda().getId();

        Animal animal = animalRepository.findByIdAndFazendaId(animalId, fazendaId)
                .orElseThrow(() -> new RuntimeException("Animal não encontrado."));

        EventoAnimal evento = new EventoAnimal();
        evento.setAnimal(animal);
        evento.setTipoEvento(requestDTO.tipoEvento());
        evento.setDataEvento(requestDTO.dataEvento());
        evento.setValor(requestDTO.valor());
        evento.setObservacoes(requestDTO.observacoes());

        return eventoAnimalRepository.save(evento);
    }

    //Método privado para o cálculo do GMD
    private double calcularGMD(List<EventoAnimal> historico) {
        List<EventoAnimal> pesagens = historico.stream()
                .filter(e -> e.getTipoEvento() == EventoAnimal.TipoEvento.PESAGEM && e.getValor() != null)
                .toList();

        if (pesagens.size() < 2) {
            return 0.0; // Não é possível calcular com menos de duas pesagens
        }

        EventoAnimal primeiraPesagem = pesagens.get(0);
        EventoAnimal ultimaPesagem = pesagens.get(pesagens.size() - 1);

        double ganhoDePeso = ultimaPesagem.getValor() - primeiraPesagem.getValor();

        // Calcula a diferença em dias
        long diffInMillis = ultimaPesagem.getDataEvento().getTime() - primeiraPesagem.getDataEvento().getTime();
        long dias = diffInMillis / (1000 * 60 * 60 * 24);

        if (dias <= 0) {
            return 0.0; // Evita divisão por zero se as pesagens foram no mesmo dia
        }

        return ganhoDePeso / dias;
    }
}