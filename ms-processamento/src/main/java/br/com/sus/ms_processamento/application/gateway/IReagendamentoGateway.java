package br.com.sus.ms_processamento.application.gateway;

import br.com.sus.ms_processamento.domain.model.Agendamento;
import br.com.sus.ms_processamento.domain.model.Reagendamento;
import br.com.sus.ms_processamento.domain.model.StatusReagendamentoEnum;

import java.util.List;
import java.util.UUID;

public interface IReagendamentoGateway {

    List<Reagendamento> buscarDisponivelParaReagendamento(Reagendamento reagendamento);

    void enviarOpcaoReagendamento(List<Reagendamento> reagendamento);

    Agendamento buscarAgendamentoPorId(UUID id);

    Reagendamento buscarReagendamentoPorId(UUID id);

    Reagendamento buscarReagendamentoPorAgendamentoEFila(UUID idAgendamento, UUID idFila);

    void enviarInformativoJaAlocado(Reagendamento reagendamento);

    void enviarInformativoConfirmado(Reagendamento reagendamento);

    void atualizarStatusReagendamento(UUID idReagendamento, StatusReagendamentoEnum statusReagendamentoEnum);

    Reagendamento salvar(Reagendamento reagendamento);

    List<Reagendamento> salvar(List<Reagendamento> reagendamentos);

    boolean possuiReagendamentoPendenteResposta(UUID idAgendamento);
}
