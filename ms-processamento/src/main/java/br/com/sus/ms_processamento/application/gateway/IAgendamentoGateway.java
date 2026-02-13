package br.com.sus.ms_processamento.application.gateway;

import br.com.sus.ms_processamento.domain.model.Agendamento;
import br.com.sus.ms_processamento.domain.model.StatusAgendamentoEnum;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoEntity;

import java.util.UUID;

public interface IAgendamentoGateway {

    void enviarConfirmacao(Agendamento agendamento);

    void atualizarStatusAgendamento(UUID userId, StatusAgendamentoEnum statusAgendamento);

    void salvar(Agendamento agendamento);

    void realocarAgendamento(Agendamento agendamento);

    Agendamento buscarAgendamento(UUID idAgendamento);

}
