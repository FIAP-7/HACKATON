package br.com.sus.ms_notificacao.dto;

public record AntecipacaoNotificacaoRecord(
        String especialidadeConsultaAntecipada,
        String dataHoraConsultaAntecipada,
        String enderecoConsultaAntecipada,
        String localAtendimentoConsultaAntecipada,
        String nomePacienteAntecipacao,
        String emailPacienteAntecipacao,
        String especialidadeAgendada,
        String dataHoraAgendada,
        String enderecoAgendada,
        String localAtendimentoAgendada,
        String tokenUUID
) {
}
