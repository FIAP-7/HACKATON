package br.com.sus.ms_notificacao.messaging;

import br.com.sus.ms_notificacao.dto.ConfirmacaoConsultaEvent;
import br.com.sus.ms_notificacao.dto.ConfirmacaoNotificacaoRecord;
import br.com.sus.ms_notificacao.service.ConfirmacaoNotificacaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConfirmacaoAgendamentoConsumer {

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

}
