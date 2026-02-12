package br.com.sus.ms_notificacao.messaging;

import br.com.sus.ms_notificacao.dto.ConfirmacaoAntecipacaoEvent;
import br.com.sus.ms_notificacao.service.ConfirmacaoAntecipacaoNotificacaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConfirmacaoAntecipacaoConsumer {

    private final ConfirmacaoAntecipacaoNotificacaoService confirmacaoAntecipacaoNotificacaoService;

    @RabbitListener(queues = "sus.notificacao.confirmacao-antecipacao")
    public void onMessageConfirmacaoAntecipacao(ConfirmacaoAntecipacaoEvent event) {
        log.info("[ConfirmacaoAntecipacaoConsumer] Recebido evento de confirmação de antecipação - paciente={}, email={}",
                event.paciente().nome(), event.paciente().email());
        
        confirmacaoAntecipacaoNotificacaoService.enviarEmailConfirmacaoAntecipacao(event);
    }
}
