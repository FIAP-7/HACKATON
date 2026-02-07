package br.com.sus.ms_processamento.application.usecase.reagendamento.validation;

import br.com.sus.ms_processamento.domain.model.Reagendamento;

import java.util.List;

public class ReagendamentoValidationChain {

    private final List<IReagendamentoValidation> validations;

    public ReagendamentoValidationChain(List<IReagendamentoValidation> validations) {
        this.validations = validations;
    }

    public void validate(Reagendamento reagendamento) {
        for (IReagendamentoValidation validation : validations) {
            validation.validate(reagendamento);
        }
    }
}
