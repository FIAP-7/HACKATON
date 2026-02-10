package br.com.sus.ms_processamento.infrastructure.api.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EventoRespostaUsuario(
        String identificador,
        String resposta,
        CanalNotificacao canal,
        LocalDateTime dataRecebimento
) {
    public enum CanalNotificacao { EMAIL }
}
