package br.com.sus.ms_notificacao.messaging;

import br.com.sus.ms_notificacao.dto.AntecipacaoNotificacaoRecord;
import br.com.sus.ms_notificacao.dto.ConfirmacaoNotificacaoRecord;
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

    @RabbitListener(queues = "sus.core.confirmacao")
    public void onMessageConfirmacao(ConfirmacaoNotificacaoRecord request) {
        log.info("Notificação de confirmação enviada para: {}", request.pacienteEmail());
        confirmacaoNotificacaoService.enviarEmailAgendamento(request);
    }

    @RabbitListener(queues = "sus.core.antecipacao")
    public void onMessageAntecipacao(AntecipacaoNotificacaoRecord request) {
        log.info("Notificação de confirmação enviada para: {}", request.pacienteEmail());
        antecipacaoNotificacaoService.enviarEmailAntecipacao(request);
    }

}