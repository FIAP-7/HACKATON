package br.com.sus.ms_processamento.application.usecase.agendamento.presenters;

import br.com.sus.ms_processamento.application.usecase.agendamento.dto.AgendamentoInput;
import br.com.sus.ms_processamento.application.usecase.agendamento.dto.AgendamentoOutput;
import br.com.sus.ms_processamento.domain.model.Agendamento;

public class AgendamentoPresenter {

    public static AgendamentoOutput agendamentoOutput(Agendamento agendamento) {
        return new AgendamentoOutput(agendamento.getId(),
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

    public static Agendamento toDomain(AgendamentoInput agendamentoInput) {
        return Agendamento.create(agendamentoInput.id(),
                agendamentoInput.idExterno(),
                agendamentoInput.pacienteNome(),
                agendamentoInput.pacienteTelefone(),
                agendamentoInput.pacienteEmail(),
                agendamentoInput.dataHora(),
                agendamentoInput.medico(),
                agendamentoInput.especialidade(),
                agendamentoInput.endereco(),
                agendamentoInput.localAtendimento(),
                agendamentoInput.unidadeId(),
                agendamentoInput.status(),
                agendamentoInput.dataLimiteConsulta()
        );
    }

}
