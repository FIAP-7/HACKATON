package br.com.sus.ms_processamento.infrastructure.presenters;

import br.com.sus.ms_processamento.domain.model.Agendamento;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoEntity;

public class AgendamentoEntityPresenters {
    
    public static AgendamentoEntity toEntity(Agendamento agendamento) {
        return AgendamentoEntity.builder()
                .id(agendamento.getId())
                .idExterno(agendamento.getIdExterno())
                .pacienteNome(agendamento.getPacienteNome())
                .pacienteTelefone(agendamento.getPacienteTelefone())
                .dataHoraConsulta(agendamento.getDataHoraConsulta())
                .medicoNome(agendamento.getMedicoNome())
                .especialidade(agendamento.getEspecialidade())
                .unidadeSaude(agendamento.getUnidadeSaude())
                .status(agendamento.getStatus())
                .dataLimiteConsulta(agendamento.getDataLimiteConsulta())
                .build();
    }
    
    public static Agendamento toDomain(AgendamentoEntity agendamento) {
        return Agendamento.create(agendamento.getId(),
                agendamento.getIdExterno(),
                agendamento.getPacienteNome(),
                agendamento.getPacienteTelefone(),
                agendamento.getDataHoraConsulta(),
                agendamento.getMedicoNome(),
                agendamento.getEspecialidade(),
                agendamento.getUnidadeSaude(),
                agendamento.getStatus(),
                agendamento.getDataLimiteConsulta()
        );
    }
}
