package com.meurebanho.sistema_gestao_de_bovinos_backend.animal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventoAnimalRepository extends JpaRepository<EventoAnimal, Long> {
    // Busca todos os eventos de um animal, ordenados pela data
    List<EventoAnimal> findByAnimalIdOrderByDataEventoAsc(Long animalId);

    // Busca o primeiro evento de um tipo específico para um animal, ordenado pela data mais recente
    Optional<EventoAnimal> findFirstByAnimalIdAndTipoEventoOrderByDataEventoDesc(Long animalId, EventoAnimal.TipoEvento tipoEvento);
}