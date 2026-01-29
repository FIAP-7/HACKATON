package br.com.sus.ingestao.infra.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE_NAME = "sus.direct.exchange";
    public static final String QUEUE_AGENDAMENTO = "sus.input.carga-agendamento";
    public static final String ROUTING_AGENDAMENTO = "rota.carga.agendamento";

    @Bean
    public DirectExchange susDirectExchange() {
        return new DirectExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue agendamentoQueue() {
        return new Queue(QUEUE_AGENDAMENTO, true);
    }

    @Bean
    public Binding agendamentoBinding(Queue agendamentoQueue, DirectExchange susDirectExchange) {
        return BindingBuilder.bind(agendamentoQueue).to(susDirectExchange).with(ROUTING_AGENDAMENTO);
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
