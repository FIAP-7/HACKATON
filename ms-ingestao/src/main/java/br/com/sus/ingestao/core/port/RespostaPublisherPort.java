package br.com.sus.ingestao.core.port;

import br.com.sus.ingestao.core.event.EventoRespostaUsuario;

public interface RespostaPublisherPort {
    void publicar(EventoRespostaUsuario event);
}
