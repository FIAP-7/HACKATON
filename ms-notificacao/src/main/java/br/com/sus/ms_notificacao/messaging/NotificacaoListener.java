package br.com.sus.ms_notificacao.messaging;

import br.com.sus.ms_notificacao.dto.AntecipacaoNotificacaoRecord;
import br.com.sus.ms_notificacao.dto.AntecipacaoConsultaEvent;
import br.com.sus.ms_notificacao.dto.ConfirmacaoNotificacaoRecord;
import br.com.sus.ms_notificacao.dto.ConfirmacaoConsultaEvent;
import br.com.sus.ms_notificacao.service.AntecipacaoNotificacaoService;
import br.com.sus.ms_notificacao.service.ConfirmacaoNotificacaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificacaoListener {

    private final AntecipacaoNotificacaoService antecipacaoNotificacaoService;
    private final ConfirmacaoNotificacaoService confirmacaoNotificacaoService;

    @RabbitListener(queues = "sus.processamento.confirmacao-usuario")
    public void onMessageConfirmacao(ConfirmacaoConsultaEvent event) {
        ConfirmacaoNotificacaoRecord request = new ConfirmacaoNotificacaoRecord(
                event.paciente().nome(),
                event.paciente().email(),
                event.consulta().especialidade(),
                event.consulta().dataHora() != null ? event.consulta().dataHora().toString() : null,
                event.consulta().localAtendimento(),
                event.consulta().endereco(),
                event.tokenUUID()
        );

        log.info("Notificação de confirmação enviada para nome={} email={}", request.pacienteNome(), request.pacienteEmail());
        confirmacaoNotificacaoService.enviarEmailAgendamento(request);
    }

    @RabbitListener(queues = "sus.processamento.antecipacao-usuario")
    public void onMessageAntecipacao(AntecipacaoConsultaEvent event) {
        try {
            log.info("chegou ANTECIPACAO para o nome={}", event.consultaAntecipada().paciente().nome());
            log.info("chegou ANTECIPACAO para o haraAtual={}", event.consultaAntecipada().consulta().dataHora());
            log.info("chegou ANTECIPACAO para o novoHorario={}", event.consultaCancelada().consulta().dataHora());
            AntecipacaoNotificacaoRecord request = AntecipacaoConsultaEvent.toRecord(event);
            antecipacaoNotificacaoService.enviarEmailAntecipacao(request);
            log.info("Notificação de antecipação enviada para: nome={} email={}", request.nomePacienteAntecipacao(), request.emailPacienteAntecipacao());
        } catch (Exception e) {
            log.error("Erro ao processar antecipação", e);
            throw e;
        }
    }

}