package br.com.sus.ms_processamento.application.usecase.agendamento.validation;

import br.com.sus.ms_processamento.application.gateway.IAgendamentoGateway;
import br.com.sus.ms_processamento.domain.model.Agendamento;
import br.com.sus.ms_processamento.domain.model.StatusAgendamentoEnum;

public class AgendamentoCanceladoHandler implements IAgendamentoValidation {

    private final IAgendamentoGateway agendamentoGateway;

    public AgendamentoCanceladoHandler(IAgendamentoGateway agendamentoGateway) {
        this.agendamentoGateway = agendamentoGateway;
    }

    @Override
    public void validate(Agendamento agendamento) {
        if(StatusAgendamentoEnum.CANCELADO.equals(agendamento.getStatus())) {
            agendamentoGateway.realocarAgendamento(agendamento);
        }
    }
}
