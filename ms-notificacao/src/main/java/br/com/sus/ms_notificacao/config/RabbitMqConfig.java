package br.com.sus.ms_notificacao.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE_NAME = "sus.direct.exchange";
   
    public static final String QUEUE_CONFIRMACAO = "sus.processamento.confirmacao-usuario";
    public static final String ROUTING_CONFIRMACAO = "rota.processamento.confirmacao.usuario";
    
    public static final String QUEUE_ANTECIPACAO = "sus.processamento.antecipacao-usuario";
    public static final String ROUTING_ANTECIPACAO = "rota.processamento.antecipacao.usuario";

    @Bean
    public DirectExchange susDirectExchange() {
        return new DirectExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue confirmacaoQueue() {
        return new Queue(QUEUE_CONFIRMACAO, true);
    }

    @Bean
    public Binding confirmacaoBinding(Queue confirmacaoQueue, DirectExchange susDirectExchange) {
        return BindingBuilder.bind(confirmacaoQueue).to(susDirectExchange).with(ROUTING_CONFIRMACAO);
    }

    @Bean
    public Queue antecipacaoQueue() {
        return new Queue(QUEUE_ANTECIPACAO, true);
    }

    @Bean
    public Binding antecipacaoBinding(Queue antecipacaoQueue, DirectExchange susDirectExchange) {
        return BindingBuilder.bind(antecipacaoQueue).to(susDirectExchange).with(ROUTING_ANTECIPACAO);
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
