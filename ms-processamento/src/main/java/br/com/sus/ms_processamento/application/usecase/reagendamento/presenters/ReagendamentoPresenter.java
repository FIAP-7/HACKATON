package br.com.sus.ms_processamento.application.usecase.reagendamento.presenters;

import br.com.sus.ms_processamento.application.usecase.reagendamento.dto.ReagendamentoInput;
import br.com.sus.ms_processamento.domain.model.Reagendamento;

public class ReagendamentoPresenter {

    public static Reagendamento toReagendamento(ReagendamentoInput reagendamentoInput) {
        return Reagendamento.create(reagendamentoInput.id(),
                reagendamentoInput.status(),
                reagendamentoInput.idAgendamento(),
                reagendamentoInput.idFilaEspera()
        );
    }

}
