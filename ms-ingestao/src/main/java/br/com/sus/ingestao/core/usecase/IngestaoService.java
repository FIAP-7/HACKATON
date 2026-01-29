package br.com.sus.ingestao.core.usecase;

import br.com.sus.ingestao.entrypoint.dto.AgendamentoRequest;

public interface IngestaoService {
    void processarAgendamento(AgendamentoRequest request);
}
