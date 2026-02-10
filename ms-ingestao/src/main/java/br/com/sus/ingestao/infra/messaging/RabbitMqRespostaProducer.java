package br.com.sus.ingestao.infra.messaging;

import br.com.sus.ingestao.core.event.EventoRespostaUsuario;
import br.com.sus.ingestao.core.port.RespostaPublisherPort;
import br.com.sus.ingestao.infra.config.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqRespostaProducer implements RespostaPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqRespostaProducer.class);
    private final RabbitTemplate rabbitTemplate;

    public RabbitMqRespostaProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publicar(EventoRespostaUsuario event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.ROUTING_RESPOSTA_USUARIO,
                event
        );
        log.info("[RabbitMQ] RespostaUsuario publicada exchange={}, routingKey={}, identificador={}, canal={}",
                RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.ROUTING_RESPOSTA_USUARIO,
                event.identificador(),
                event.canal());
    }

    @Override
    public void publicarAntecipacao(EventoRespostaUsuario event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.ROUTING_ANTECIPACAO_USUARIO,
                event
        );
        log.info("[RabbitMQ] Antecipação publicada exchange={}, routingKey={}, identificador={}, canal={}",
                RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.ROUTING_ANTECIPACAO_USUARIO,
                event.identificador(),
                event.canal());
    }


}
