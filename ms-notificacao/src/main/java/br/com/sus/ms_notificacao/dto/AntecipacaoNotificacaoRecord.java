package br.com.sus.ms_notificacao.dto;

public record AntecipacaoNotificacaoRecord(
        String pacienteNome,
        String pacienteEmail,
        String especialidade,
        String dataNova,
        String dataAtual,
        String localAtendimento,
        String endereco,
        String tokenUUID

) {
}
