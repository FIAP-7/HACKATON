package br.com.sus.ms_processamento.infrastructure.api;

import br.com.sus.ms_processamento.application.usecase.agendamento.dto.AgendamentoInput;
import br.com.sus.ms_processamento.domain.model.Agendamento;
import br.com.sus.ms_processamento.domain.model.StatusAgendamentoEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public record AgendamentoRequest(UUID id,
                                 String idExterno,
                                 String pacienteNome,
                                 String pacienteTelefone,
                                 LocalDateTime dataHoraConsulta,
                                 String medicoNome,
                                 String especialidade,
                                 String unidadeSaude,
                                 StatusAgendamentoEnum status,
                                 LocalDateTime dataLimiteConsulta)
{
    public AgendamentoInput toInput() {
        return new AgendamentoInput(id,
                idExterno,
                pacienteNome,
                pacienteTelefone,
                dataHoraConsulta,
                medicoNome,
                especialidade,
                unidadeSaude,
                status,
                dataLimiteConsulta);
    }

}
