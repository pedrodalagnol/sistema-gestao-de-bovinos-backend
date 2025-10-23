package com.meurebanho.sistema_gestao_de_bovinos_backend.animal;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference
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
        PESAGEM,         // Registro de peso do animal
        VACINACAO,       // Aplicação de vacina
        VERMIFUGACAO,    // Aplicação de vermífugo
        MEDICACAO,       // Outros tratamentos ou medicações
        INSEMINACAO,     // Inseminação artificial
        DIAGNOSTICO_GESTACAO, // Diagnóstico de prenhez
        PARTO,           // Registro de parto
        IDENTIFICACAO,   // Aplicação de brinco, marcação a fogo, etc.
        MUDANCA_LOTE,    // Movimentação do animal entre lotes/pastos
        ENTRADA_LOTE,    // Entrada do animal no rebanho
        SAIDA_LOTE,      // Saída do animal do rebanho (sem ser venda ou morte)
        VENDA,           // Venda do animal
        MORTE            // Morte do animal
    }
}