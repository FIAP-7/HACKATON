package br.com.sus.ms_processamento.infrastructure.api.producer;

import br.com.sus.ms_processamento.infrastructure.api.event.ConfirmacaoConsultaEvent;
import br.com.sus.ms_processamento.infrastructure.config.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ConfirmacaoConsultaProducer {

    private static final Logger log = LoggerFactory.getLogger(ConfirmacaoConsultaProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public ConfirmacaoConsultaProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarConfirmacao(ConfirmacaoConsultaEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.ROUTING_CONFIRMACAO,
                event
        );
        log.info("[RabbitMQ] Evento de confirmação publicado na exchange={}, routingKey={}, idExterno={}, tokenUUID={}, nome={}, email={}, localAtendimento={}",
                RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.ROUTING_CONFIRMACAO,
                event.idExterno(),
                event.tokenUUID(),
                event.paciente().nome(),
                event.paciente().email(),
                event.consulta().localAtendimento());
    }
}
