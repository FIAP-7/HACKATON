package br.com.sus.ingestao.core.event;

import java.time.LocalDateTime;

public record AgendamentoEvent(
        String idExterno,
        Paciente paciente,
        Consulta consulta,
        LocalDateTime dataIngestao
) {
    public record Paciente(String nome, String telefone) {}
    public record Consulta(LocalDateTime dataHora, String medico, String especialidade, String unidadeId) {}
}
