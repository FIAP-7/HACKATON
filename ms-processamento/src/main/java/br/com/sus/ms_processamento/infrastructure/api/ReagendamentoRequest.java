package br.com.sus.ms_processamento.infrastructure.api;

import br.com.sus.ms_processamento.application.usecase.reagendamento.dto.ReagendamentoInput;

import java.util.UUID;

public record ReagendamentoRequest(UUID idAgendamento) {

    public ReagendamentoInput toCriarReagendamentoInput() {
        return new ReagendamentoInput(null, null, idAgendamento, null);
    }
}
