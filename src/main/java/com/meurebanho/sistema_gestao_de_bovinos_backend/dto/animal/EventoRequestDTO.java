package com.meurebanho.sistema_gestao_de_bovinos_backend.dto.animal;

import com.meurebanho.sistema_gestao_de_bovinos_backend.animal.EventoAnimal.TipoEvento;
import java.util.Date;

public record EventoRequestDTO(
        TipoEvento tipoEvento,
        Date dataEvento,
        Double valor,
        String observacoes
) {}