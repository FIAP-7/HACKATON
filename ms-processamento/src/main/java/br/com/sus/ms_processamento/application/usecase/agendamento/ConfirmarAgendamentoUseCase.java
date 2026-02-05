package br.com.sus.ms_processamento.application.usecase.agendamento;

import br.com.sus.ms_processamento.application.gateway.IAgendamentoGateway;
import br.com.sus.ms_processamento.application.usecase.agendamento.dto.AgendamentoInput;
import br.com.sus.ms_processamento.application.usecase.agendamento.dto.AgendamentoOutput;
import br.com.sus.ms_processamento.application.usecase.agendamento.presenters.AgendamentoPresenter;
import br.com.sus.ms_processamento.application.usecase.agendamento.validation.AgendamentoValidationChain;
import br.com.sus.ms_processamento.domain.model.Agendamento;

public class ConfirmarAgendamentoUseCase {

    private final IAgendamentoGateway agendamentoGateway;
    private final AgendamentoValidationChain agendamentoValidationChain;

    private ConfirmarAgendamentoUseCase(IAgendamentoGateway agendamentoGateway, AgendamentoValidationChain agendamentoValidationChain) {
        this.agendamentoGateway = agendamentoGateway;
        this.agendamentoValidationChain = agendamentoValidationChain;
    }

    public static ConfirmarAgendamentoUseCase create(IAgendamentoGateway agendamentoGateway, AgendamentoValidationChain agendamentoValidationChain) {
        return new ConfirmarAgendamentoUseCase(agendamentoGateway, agendamentoValidationChain);
    }

    public AgendamentoOutput execute(AgendamentoInput agendamentoInput) {
        Agendamento agendamento = AgendamentoPresenter.toDomain(agendamentoInput);

        agendamentoValidationChain.validate(agendamento);

        return AgendamentoPresenter.agendamentoOutput(agendamento);
    }

}
