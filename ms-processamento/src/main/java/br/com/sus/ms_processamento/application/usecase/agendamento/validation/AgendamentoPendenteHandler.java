package br.com.sus.ms_processamento.application.usecase.agendamento.validation;

import br.com.sus.ms_processamento.application.gateway.IAgendamentoGateway;
import br.com.sus.ms_processamento.domain.model.Agendamento;
import br.com.sus.ms_processamento.domain.model.StatusAgendamentoEnum;

import java.time.LocalDateTime;

public class AgendamentoPendenteHandler implements IAgendamentoValidation {

    private final IAgendamentoGateway agendamentoGateway;

    public AgendamentoPendenteHandler(IAgendamentoGateway agendamentoGateway) {
        this.agendamentoGateway = agendamentoGateway;
    }

    @Override
    public void validate(Agendamento agendamento) {
        if (StatusAgendamentoEnum.PENDENTE.equals(agendamento.getStatus())) {
            LocalDateTime dataConfirmar = agendamento.getDataHora().minusDays(7);

            if(dataConfirmar.isBefore(LocalDateTime.now()) && agendamento.getDataHora().isAfter(LocalDateTime.now())) {
                agendamento.setStatus(StatusAgendamentoEnum.AGUARDANDO_CONFIRMACAO);

                agendamentoGateway.salvar(agendamento);
                agendamentoGateway.enviarConfirmacao(agendamento);
            } else {
                if (agendamento.getId() == null) {
                    agendamentoGateway.salvar(agendamento);
                }
            }
        }
    }
}
