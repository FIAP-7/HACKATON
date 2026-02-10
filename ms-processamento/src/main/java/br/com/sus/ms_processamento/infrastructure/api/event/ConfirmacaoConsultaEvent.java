package br.com.sus.ms_processamento.infrastructure.api.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConfirmacaoConsultaEvent(
        String tokenUUID,
        String idExterno,
        Paciente paciente,
        Consulta consulta,
        LocalDateTime dataIngestao
) {
    public record Paciente(String nome, String telefone, String email) {}
    public record Consulta(LocalDateTime dataHora, String medico, String especialidade, String endereco, String localAtendimento, String unidadeId) {}

    /**
     * Factory method para criar ConfirmacaoConsultaEvent a partir de AgendamentoEvent
     */
    public static ConfirmacaoConsultaEvent from(AgendamentoEvent event, String tokenUUID) {
        return new ConfirmacaoConsultaEvent(
                tokenUUID,
                event.idExterno(),
                new Paciente(
                        event.paciente().nome(),
                        event.paciente().telefone(),
                        event.paciente().email()
                ),
                new Consulta(
                        event.consulta().dataHora(),
                        event.consulta().medico(),
                        event.consulta().especialidade(),
                        event.consulta().endereco(),
                        event.consulta().localAtendimento(),
                        event.consulta().unidadeId()
                ),
                event.dataIngestao()
        );
    }
}
