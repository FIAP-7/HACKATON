package br.com.sus.ms_processamento.application.usecase.agendamento.validation;

import br.com.sus.ms_processamento.domain.model.Agendamento;

import java.util.List;

public class AgendamentoValidationChain {

    private final List<IAgendamentoValidation> validations;

    public AgendamentoValidationChain(List<IAgendamentoValidation> validations) {
        this.validations = validations;
    }

    public void validate(Agendamento agendamento) {
        for (IAgendamentoValidation validation : validations) {
            validation.validate(agendamento);
        }
    }
}
