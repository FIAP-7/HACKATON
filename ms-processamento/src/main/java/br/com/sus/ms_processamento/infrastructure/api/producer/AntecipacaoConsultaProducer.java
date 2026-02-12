package br.com.sus.ms_processamento.infrastructure.api.producer;

import br.com.sus.ms_processamento.infrastructure.api.event.AntecipacaoConsultaEvent;
import br.com.sus.ms_processamento.infrastructure.api.event.ConfirmacaoConsultaEvent;
import br.com.sus.ms_processamento.infrastructure.api.event.ConfirmacaoAntecipacaoEvent;
import br.com.sus.ms_processamento.infrastructure.config.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class AntecipacaoConsultaProducer {

    private static final Logger log = LoggerFactory.getLogger(AntecipacaoConsultaProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public AntecipacaoConsultaProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarAntecipacao(AntecipacaoConsultaEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.ROUTING_ANTECIPACAO,
                event
        );
        log.info("[RabbitMQ] Evento de antecipação publicado na exchange={}, routingKey={}, pacienteCancelamento={}, pacienteAntecipacao={}, email={}, localAtendimento={}, dataHoraCancelamento={}",
                RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.ROUTING_ANTECIPACAO,
                event.consultaCancelada().paciente().nome(),
                event.consultaAntecipada().paciente().nome(),
                event.consultaAntecipada().paciente().email(),
                event.consultaAntecipada().consulta().localAtendimento(),
                event.consultaCancelada().consulta().dataHora()
                );
    }

    public void enviarConfirmacaoAntecipacao(ConfirmacaoAntecipacaoEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.ROUTING_CONFIRMACAO_ANTECIPACAO,
                event
        );
        log.info("[RabbitMQ] Evento de confirmação de antecipação publicado na exchange={}, routingKey={}, paciente={}, email={}, novaDataHora={}",
                RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.ROUTING_CONFIRMACAO_ANTECIPACAO,
                event.paciente().nome(),
                event.paciente().email(),
                event.novaDataHora()
                );
    }
}
