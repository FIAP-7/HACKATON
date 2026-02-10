package br.com.sus.ms_processamento.application.usecase.agendamento.dto;

import br.com.sus.ms_processamento.domain.model.StatusAgendamentoEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public record AgendamentoOutput(UUID id,
                                String idExterno,
                                String pacienteNome,
                                String pacienteTelefone,
                                String pacienteEmail,
                                LocalDateTime dataHora,
                                String medico,
                                String especialidade,
                                String endereco,
                                String localAtendimento,
                                String unidadeId,
                                StatusAgendamentoEnum status,
                                LocalDateTime dataLimiteConsulta) {
}
