package br.com.sus.ms_processamento.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE_NAME = "sus.direct.exchange";

    public static final String QUEUE_AGENDAMENTO = "sus.input.carga-agendamento";
    public static final String ROUTING_AGENDAMENTO = "rota.carga.agendamento";

    public static final String QUEUE_RESPOSTA_USUARIO = "sus.input.resposta-usuario";
    public static final String ROUTING_RESPOSTA_USUARIO = "rota.resposta.usuario";

    public static final String QUEUE_CONFIRMACAO = "sus.processamento.confirmacao-usuario";
    public static final String ROUTING_CONFIRMACAO = "rota.processamento.confirmacao.usuario";

    public static final String QUEUE_ANTECIPACAO = "sus.processamento.antecipacao-usuario";
    public static final String ROUTING_ANTECIPACAO = "rota.processamento.antecipacao.usuario";

    public static final String QUEUE_ANTECIPACAO_USUARIO = "sus.input.antecipacao-usuario";
    public static final String ROUTING_ANTECIPACAO_USUARIO = "rota.input.antecipacao.usuario";

    public static final String QUEUE_CONFIRMACAO_ANTECIPACAO = "sus.notificacao.confirmacao-antecipacao";
    public static final String ROUTING_CONFIRMACAO_ANTECIPACAO = "rota.notificacao.confirmacao.antecipacao";

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
    public Queue confirmacaoQueue() {
        return new Queue(QUEUE_CONFIRMACAO, true);
    }

    @Bean
    public Binding confirmacaoBinding(Queue confirmacaoQueue, DirectExchange susDirectExchange) {
        return BindingBuilder.bind(confirmacaoQueue).to(susDirectExchange).with(ROUTING_CONFIRMACAO);
    }

    @Bean
    public Queue antecipacaoQueue() { return new Queue(QUEUE_ANTECIPACAO, true); }

    @Bean
    public Binding antecipacaoBinding(Queue antecipacaoQueue, DirectExchange susDirectExchange) {
        return BindingBuilder.bind(antecipacaoQueue).to(susDirectExchange).with(ROUTING_ANTECIPACAO);
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
    public Queue antecipacaoUsuarioQueue() {
        return new Queue(QUEUE_ANTECIPACAO_USUARIO, true);
    }

    @Bean
    public Binding antecipacaoUsuarioBinding(Queue respostaUsuarioQueue, DirectExchange susDirectExchange) {
        return BindingBuilder.bind(respostaUsuarioQueue).to(susDirectExchange).with(ROUTING_ANTECIPACAO_USUARIO);
    }

    @Bean
    public Queue confirmacaoAntecipacaoQueue() {
        return new Queue(QUEUE_CONFIRMACAO_ANTECIPACAO, true);
    }

    @Bean
    public Binding confirmacaoAntecipacaoBinding(Queue confirmacaoAntecipacaoQueue, DirectExchange susDirectExchange) {
        return BindingBuilder.bind(confirmacaoAntecipacaoQueue).to(susDirectExchange).with(ROUTING_CONFIRMACAO_ANTECIPACAO);
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
