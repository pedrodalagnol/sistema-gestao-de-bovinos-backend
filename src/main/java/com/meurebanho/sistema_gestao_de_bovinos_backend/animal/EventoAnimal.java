package com.meurebanho.sistema_gestao_de_bovinos_backend.animal;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "eventos_animal")
@Data
@NoArgsConstructor
public class EventoAnimal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEvento tipoEvento;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dataEvento;

    // Usado para peso, dose de medicamento, etc.
    private Double valor;

    private String observacoes;

    // Enum para os tipos de evento
    public enum TipoEvento {
        PESAGEM,
        MEDICACAO,
        NASCIMENTO,
        MORTE,
        VENDA,
        TRANSFERENCIA
    }
}