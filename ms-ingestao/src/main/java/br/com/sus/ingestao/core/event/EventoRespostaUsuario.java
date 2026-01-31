package br.com.sus.ingestao.core.event;

import java.time.LocalDateTime;

public record EventoRespostaUsuario(
        String telefone,
        String resposta,
        LocalDateTime dataRecebimento
) {
}
