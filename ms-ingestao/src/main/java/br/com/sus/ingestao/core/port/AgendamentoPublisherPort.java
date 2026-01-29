package br.com.sus.ingestao.core.port;

import br.com.sus.ingestao.core.event.AgendamentoEvent;

public interface AgendamentoPublisherPort {
    void publicar(AgendamentoEvent event);
}
