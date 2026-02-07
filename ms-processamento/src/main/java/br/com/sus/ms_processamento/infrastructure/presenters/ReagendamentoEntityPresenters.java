package br.com.sus.ms_processamento.infrastructure.presenters;

import br.com.sus.ms_processamento.domain.model.Reagendamento;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.FilaEsperaEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.ReagendamentoEntity;

public class ReagendamentoEntityPresenters {

    public static Reagendamento toDomain(ReagendamentoEntity reagendamentoEntity) {
        return Reagendamento.create(
                reagendamentoEntity.getId(),
                reagendamentoEntity.getStatus(),
                reagendamentoEntity.getAgendamento().getId(),
                reagendamentoEntity.getFilaEspera().getId()
        );
    }

    public static ReagendamentoEntity toEntity(Reagendamento reagendamento) {
        AgendamentoEntity agendamentoEntity = AgendamentoEntityPresenters.toEntity(reagendamento.getAgendamento());

        FilaEsperaEntity filaEsperaEntity = FilaEsperaEntityPresenters.toEntity(reagendamento.getFilaEspera());

        return ReagendamentoEntity.builder()
                .id(reagendamento.getId())
                .status(reagendamento.getStatus())
                .agendamento(agendamentoEntity)
                .filaEspera(filaEsperaEntity)
                .build();
    }
}
