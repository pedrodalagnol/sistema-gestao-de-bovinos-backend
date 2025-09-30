package com.meurebanho.sistema_gestao_de_bovinos_backend.animal;

import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.animal.AnimalDetailsResponseDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.animal.AnimalRequestDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.animal.AnimalResponseDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.animal.EventoRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/animais")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService animalService;

    @PostMapping
    public ResponseEntity<AnimalResponseDTO> createAnimal(@RequestBody AnimalRequestDTO requestDTO) {
        AnimalResponseDTO novoAnimal = animalService.createAnimal(requestDTO);
        return new ResponseEntity<>(novoAnimal, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AnimalResponseDTO>> getMeusAnimais() {
        List<AnimalResponseDTO> animais = animalService.getAnimaisByFazenda();
        return ResponseEntity.ok(animais);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnimalResponseDTO> updateAnimal(@PathVariable Long id, @RequestBody AnimalRequestDTO requestDTO) {
        AnimalResponseDTO animalAtualizado = animalService.updateAnimal(id, requestDTO);
        return ResponseEntity.ok(animalAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnimal(@PathVariable Long id) {
        animalService.deleteAnimal(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content, que é o padrão para delete com sucesso
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalDetailsResponseDTO> getAnimalDetails(@PathVariable Long id) {
        return ResponseEntity.ok(animalService.getAnimalDetails(id));
    }

    @PostMapping("/{id}/eventos")
    public ResponseEntity<EventoAnimal> addEvento(@PathVariable Long id, @RequestBody EventoRequestDTO requestDTO) {
        EventoAnimal novoEvento = animalService.addEvento(id, requestDTO);
        return new ResponseEntity<>(novoEvento, HttpStatus.CREATED);
    }
}