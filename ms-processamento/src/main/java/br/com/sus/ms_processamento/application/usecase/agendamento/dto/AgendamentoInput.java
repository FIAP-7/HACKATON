package br.com.sus.ms_processamento.application.usecase.agendamento.dto;

import br.com.sus.ms_processamento.domain.model.Agendamento;
import br.com.sus.ms_processamento.domain.model.StatusAgendamentoEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public record AgendamentoInput(UUID id,
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

    /*
    public Agendamento toDomain() {
        return Agendamento.create(id,
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
     */

}
