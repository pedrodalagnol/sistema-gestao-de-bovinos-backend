package com.meurebanho.sistema_gestao_de_bovinos_backend.pasto;

import com.meurebanho.sistema_gestao_de_bovinos_backend.lote.Lote;
import com.meurebanho.sistema_gestao_de_bovinos_backend.usuario.Fazenda;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "pastos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "area_ha", nullable = false)
    private Double areaHa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PastoStatus status;

    @Column(name = "data_inicio_status")
    private LocalDate dataInicioStatus;

    @OneToOne
    @JoinColumn(name = "lote_alocado_id")
    private Lote loteAlocado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fazenda_id", nullable = false)
    private Fazenda fazenda;
}
