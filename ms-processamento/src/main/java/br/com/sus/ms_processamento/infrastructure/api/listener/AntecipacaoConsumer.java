package br.com.sus.ms_processamento.infrastructure.api.listener;

import br.com.sus.ms_processamento.infrastructure.api.event.EventoRespostaUsuario;
import br.com.sus.ms_processamento.application.usecase.antecipacao.ProcessarRespostaAntecipacaoUseCase;
import br.com.sus.ms_processamento.infrastructure.config.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AntecipacaoConsumer {

    private static final Logger log = LoggerFactory.getLogger(AntecipacaoConsumer.class);

    private final ProcessarRespostaAntecipacaoUseCase processarRespostaAntecipacaoUseCase;

    public AntecipacaoConsumer(ProcessarRespostaAntecipacaoUseCase processarRespostaAntecipacaoUseCase) {
        this.processarRespostaAntecipacaoUseCase = processarRespostaAntecipacaoUseCase;
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE_ANTECIPACAO_USUARIO)
    public void processar(EventoRespostaUsuario event) {
        try {
            validarEvento(event);
            
            log.info("[RabbitMQ] AntecipacaoConsumer - token={} resposta={} canal={} dataRecebimento={}",
                    event.identificador(), event.resposta(), event.canal(), event.dataRecebimento());

            processarRespostaAntecipacaoUseCase.executar(event.identificador(), event.resposta());

        } catch (IllegalArgumentException e) {
            log.warn("[AntecipacaoConsumer] Validação falhou: {}", e.getMessage());
        } catch (Exception e) {
            log.error("[AntecipacaoConsumer] Erro ao processar antecipacao", e);
            throw e;
        }
    }

    private void validarEvento(EventoRespostaUsuario event) {
        if (event == null) {
            throw new IllegalArgumentException("Evento nulo recebido");
        }
        if (event.identificador() == null || event.identificador().isBlank()) {
            throw new IllegalArgumentException("Evento sem token recebido");
        }
    }
}

