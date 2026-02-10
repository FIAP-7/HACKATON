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
                .pacienteEmail(agendamento.getPacienteEmail())
                .dataHora(agendamento.getDataHora())
                .medico(agendamento.getMedico())
                .especialidade(agendamento.getEspecialidade())
                .endereco(agendamento.getEndereco())
                .localAtendimento(agendamento.getLocalAtendimento())
                .unidadeId(agendamento.getUnidadeId())
                .status(agendamento.getStatus())
                .dataLimiteConsulta(agendamento.getDataLimiteConsulta())
                .build();
    }
    
    public static Agendamento toDomain(AgendamentoEntity agendamento) {
        return Agendamento.create(agendamento.getId(),
                agendamento.getIdExterno(),
                agendamento.getPacienteNome(),
                agendamento.getPacienteTelefone(),
                agendamento.getPacienteEmail(),
                agendamento.getDataHora(),
                agendamento.getMedico(),
                agendamento.getEspecialidade(),
                agendamento.getEndereco(),
                agendamento.getLocalAtendimento(),
                agendamento.getUnidadeId(),
                agendamento.getStatus(),
                agendamento.getDataLimiteConsulta()
        );
    }
}
