package br.com.sus.ingestao.core.event;

import java.time.LocalDateTime;

public record EventoRespostaUsuario(
        String identificador,
        String resposta,
        CanalNotificacao canal,
        LocalDateTime dataRecebimento
) {
    public enum CanalNotificacao { EMAIL }
}
