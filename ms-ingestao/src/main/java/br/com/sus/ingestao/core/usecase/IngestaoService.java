package br.com.sus.ingestao.core.usecase;

import br.com.sus.ingestao.core.usecase.model.AgendamentoCommand;

public interface IngestaoService {
    void processarAgendamento(AgendamentoCommand command);
    void processarAcaoEmail(String token, String acao);
}
