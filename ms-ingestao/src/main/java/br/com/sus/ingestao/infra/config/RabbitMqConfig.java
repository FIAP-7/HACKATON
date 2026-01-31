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

    public static final String QUEUE_RESPOSTA_USUARIO = "sus.input.resposta-usuario";
    public static final String ROUTING_RESPOSTA_USUARIO = "rota.resposta.usuario";

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
    public Queue respostaUsuarioQueue() {
        return new Queue(QUEUE_RESPOSTA_USUARIO, true);
    }

    @Bean
    public Binding respostaUsuarioBinding(Queue respostaUsuarioQueue, DirectExchange susDirectExchange) {
        return BindingBuilder.bind(respostaUsuarioQueue).to(susDirectExchange).with(ROUTING_RESPOSTA_USUARIO);
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
