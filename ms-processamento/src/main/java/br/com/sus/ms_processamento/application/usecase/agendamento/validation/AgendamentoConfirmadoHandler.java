package br.com.sus.ms_processamento.application.usecase.agendamento.validation;

import br.com.sus.ms_processamento.application.gateway.IAgendamentoGateway;
import br.com.sus.ms_processamento.domain.model.Agendamento;
import br.com.sus.ms_processamento.domain.model.StatusAgendamentoEnum;

public class AgendamentoConfirmadoHandler implements IAgendamentoValidation {

    private final IAgendamentoGateway agendamentoGateway;

    public AgendamentoConfirmadoHandler(IAgendamentoGateway agendamentoGateway) {
        this.agendamentoGateway = agendamentoGateway;
    }

    @Override
    public void validate(Agendamento agendamento) {
        if (StatusAgendamentoEnum.CONFIRMADO_PACIENTE.equals(agendamento.getStatus())) {
            agendamentoGateway.atualizarStatusAgendamento(agendamento.getId(), StatusAgendamentoEnum.CONFIRMADO_PACIENTE);
        }
    }
}
