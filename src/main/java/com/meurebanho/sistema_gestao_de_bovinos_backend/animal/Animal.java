package com.meurebanho.sistema_gestao_de_bovinos_backend.animal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.meurebanho.sistema_gestao_de_bovinos_backend.lote.Lote;
import com.meurebanho.sistema_gestao_de_bovinos_backend.usuario.Fazenda;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "animais")
@Data
@NoArgsConstructor
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fazenda_id", nullable = false)
    private Fazenda fazenda;

    @Column(nullable = false)
    private String identificador;

    private String sexo;
    private String raca;

    @Temporal(TemporalType.DATE)
    private Date dataNascimento;

    private String status = "Ativo"; // Valor padrão

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    @JsonBackReference
    private Lote lote;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<EventoAnimal> eventos = new ArrayList<>();

    // Adicione os relacionamentos de Lote, Pai e Mãe quando for implementar essas features
}