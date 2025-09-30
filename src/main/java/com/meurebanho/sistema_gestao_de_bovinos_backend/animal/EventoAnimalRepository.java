package com.meurebanho.sistema_gestao_de_bovinos_backend.animal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoAnimalRepository extends JpaRepository<EventoAnimal, Long> {
    // Busca todos os eventos de um animal, ordenados pela data
    List<EventoAnimal> findByAnimalIdOrderByDataEventoAsc(Long animalId);
}