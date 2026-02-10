package br.com.sus.ms_processamento.infrastructure.api.event;

import java.time.LocalDateTime;

public record AgendamentoEvent(
        String idExterno,
        Paciente paciente,
        Consulta consulta,
        LocalDateTime dataIngestao
) {
    public record Paciente(String nome, String telefone, String email) {}
    public record Consulta(LocalDateTime dataHora, String medico, String especialidade, String endereco, String localAtendimento, String unidadeId) {}
}
