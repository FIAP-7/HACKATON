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
                agendamento.getDataHoraConsulta(),
                agendamento.getMedicoNome(),
                agendamento.getEspecialidade(),
                agendamento.getUnidadeSaude(),
                agendamento.getStatus(),
                agendamento.getDataLimiteConsulta()
        );
    }

    public static Agendamento toDomain(AgendamentoInput agendamentoInput) {
        return Agendamento.create(agendamentoInput.id(),
                agendamentoInput.idExterno(),
                agendamentoInput.pacienteNome(),
                agendamentoInput.pacienteTelefone(),
                agendamentoInput.dataHoraConsulta(),
                agendamentoInput.medicoNome(),
                agendamentoInput.especialidade(),
                agendamentoInput.unidadeSaude(),
                agendamentoInput.status(),
                agendamentoInput.dataLimiteConsulta()
        );
    }

}
