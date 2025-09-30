package com.meurebanho.sistema_gestao_de_bovinos_backend.lote;

import com.meurebanho.sistema_gestao_de_bovinos_backend.animal.Animal;
import com.meurebanho.sistema_gestao_de_bovinos_backend.usuario.Fazenda;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lotes")
@Data
@NoArgsConstructor
public class Lote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fazenda_id", nullable = false)
    private Fazenda fazenda;

    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Animal> animais = new ArrayList<>();
}