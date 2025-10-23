package com.meurebanho.sistema_gestao_de_bovinos_backend.animal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {
    // Busca todos os animais de uma fazenda específica
    List<Animal> findByFazendaId(Long fazendaId);

    // Busca um animal pelo seu ID e o ID da fazenda, garantindo que o usuário só acesse seus próprios animais
    Optional<Animal> findByIdAndFazendaId(Long id, Long fazendaId);

    // Busca um animal e seus eventos de forma "EAGER" para evitar LazyInitializationException
    @Query("SELECT a FROM Animal a LEFT JOIN FETCH a.eventos WHERE a.id = :id AND a.fazenda.id = :fazendaId")
    Optional<Animal> findByIdWithEventos(@Param("id") Long id, @Param("fazendaId") Long fazendaId);
}