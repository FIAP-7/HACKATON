package br.com.sus.ms_notificacao.dto;

public record ConfirmacaoNotificacaoRecord(
        String pacienteNome,
        String pacienteEmail,
        String especialidade,
        String dataConsulta,
        String localAtendimento,
        String endereco,
        String tokenUUID
) {
}
