package br.com.sus.ingestao.infra.messaging;

import br.com.sus.ingestao.core.event.AgendamentoEvent;
import br.com.sus.ingestao.core.port.AgendamentoPublisherPort;
import br.com.sus.ingestao.infra.config.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqAgendamentoProducer implements AgendamentoPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqAgendamentoProducer.class);
    private final RabbitTemplate rabbitTemplate;

    public RabbitMqAgendamentoProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publicar(AgendamentoEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.ROUTING_AGENDAMENTO,
                event
        );
        log.info("[RabbitMQ] Evento publicado na exchange={}, routingKey={}, idExterno={}",
                RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.ROUTING_AGENDAMENTO,
                event.idExterno());
    }
}
