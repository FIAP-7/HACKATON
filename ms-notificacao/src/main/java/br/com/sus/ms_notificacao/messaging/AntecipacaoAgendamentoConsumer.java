package br.com.sus.ms_notificacao.messaging;

import br.com.sus.ms_notificacao.dto.AntecipacaoNotificacaoRecord;
import br.com.sus.ms_notificacao.dto.AntecipacaoConsultaEvent;
import br.com.sus.ms_notificacao.service.AntecipacaoNotificacaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AntecipacaoAgendamentoConsumer {

    private final AntecipacaoNotificacaoService antecipacaoNotificacaoService;

    @RabbitListener(queues = "sus.processamento.antecipacao-usuario")
    public void onMessageAntecipacao(AntecipacaoConsultaEvent event) {
        try {
              AntecipacaoNotificacaoRecord request = new AntecipacaoNotificacaoRecord(
                        event.consultaCancelada().consulta().especialidade(),
                        event.consultaCancelada().consulta().dataHora() != null ? event.consultaCancelada().consulta().dataHora().toString() : null,
                        event.consultaCancelada().consulta().endereco(),
                        event.consultaCancelada().consulta().localAtendimento(),
                        event.consultaAntecipada().paciente().nome(),
                        event.consultaAntecipada().paciente().email(),
                        event.consultaAntecipada().consulta().especialidade(),
                        event.consultaAntecipada().consulta().dataHora() != null ? event.consultaAntecipada().consulta().dataHora().toString() : null,
                        event.consultaAntecipada().consulta().endereco(),
                        event.consultaAntecipada().consulta().localAtendimento(),
                        event.tokenUUID());

            antecipacaoNotificacaoService.enviarEmailAntecipacao(request);
            log.info("Notificação de antecipação enviada para: nome={} email={}", request.nomePacienteAntecipacao(), request.emailPacienteAntecipacao());
        } catch (Exception e) {
            log.error("Erro ao processar antecipação", e);
            throw e;
        }
    }

}
