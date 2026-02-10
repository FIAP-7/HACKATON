package br.com.sus.ms_processamento.infrastructure.api.listener;

import br.com.sus.ms_processamento.infrastructure.api.event.AntecipacaoConsultaEvent;
import br.com.sus.ms_processamento.infrastructure.config.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AntecipacaoConsumer {

    private static final Logger log = LoggerFactory.getLogger(AntecipacaoConsumer.class);

    @RabbitListener(queues = RabbitMqConfig.QUEUE_ANTECIPACAO)
    @Transactional
    public void processarAntecipacao(AntecipacaoConsultaEvent event) {

        // TODO: Implementar lógica de antecipação
        // Exemplos:
        // - Enviar notificação para paciente sugerir antecipação
        // - Salvar em fila de antecipação para processamento posterior
        // - Atualizar UI em tempo real
    }
}
