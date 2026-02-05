package br.com.sus.ms_processamento.application.usecase.agendamento.validation;

import br.com.sus.ms_processamento.application.gateway.IAgendamentoGateway;
import br.com.sus.ms_processamento.domain.model.Agendamento;
import br.com.sus.ms_processamento.domain.model.StatusAgendamentoEnum;

public class AgendamentoPendenteHandler implements IAgendamentoValidation {

    private final IAgendamentoGateway agendamentoGateway;

    public AgendamentoPendenteHandler(IAgendamentoGateway agendamentoGateway) {
        this.agendamentoGateway = agendamentoGateway;
    }

    @Override
    public void validate(Agendamento agendamento) {
        if (StatusAgendamentoEnum.PENDENTE.equals(agendamento.getStatus())) {
            agendamento.setStatus(StatusAgendamentoEnum.AGUARDANDO_CONFIRMACAO);

            agendamentoGateway.salvar(agendamento);

            agendamentoGateway.enviarConfirmacao(agendamento);
        }
    }
}
