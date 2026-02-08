package br.com.sus.ingestao.core.usecase.model;

import java.time.LocalDateTime;

/**
 * Command object representing the use-case input for creating/publishing an agendamento.
 * Lives in the core/usecase model to avoid core depending on entrypoint DTOs.
 */
public record AgendamentoCommand(
        String idExterno,
        Paciente paciente,
        Consulta consulta
) {
    public record Paciente(String nome, String telefone, String email) {}
    public record Consulta(LocalDateTime dataHora, String medico, String especialidade, String endereco, String localAtendimento, String unidadeId) {}
}
