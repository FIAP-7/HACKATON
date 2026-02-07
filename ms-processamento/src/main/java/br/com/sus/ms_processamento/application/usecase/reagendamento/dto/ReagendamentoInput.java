package br.com.sus.ms_processamento.application.usecase.reagendamento.dto;

import br.com.sus.ms_processamento.domain.model.StatusReagendamentoEnum;

import java.util.UUID;

public record ReagendamentoInput(
        UUID id,
        StatusReagendamentoEnum status,
        UUID idAgendamento,
        UUID idFilaEspera
) {
}
