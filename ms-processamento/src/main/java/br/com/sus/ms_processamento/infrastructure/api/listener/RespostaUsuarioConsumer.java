package br.com.sus.ms_processamento.infrastructure.api.listener;

import br.com.sus.ms_processamento.application.usecase.agendamento.AgendamentoUseCase;
import br.com.sus.ms_processamento.infrastructure.api.event.EventoRespostaUsuario;
import br.com.sus.ms_processamento.infrastructure.api.producer.AntecipacaoConsultaProducer;
import br.com.sus.ms_processamento.infrastructure.config.RabbitMqConfig;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.repository.AgendamentoJPARepository;
import br.com.sus.ms_processamento.domain.model.StatusAgendamentoEnum;
import br.com.sus.ms_processamento.infrastructure.presenters.AgendamentoEntityPresenters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class RespostaUsuarioConsumer {

    private static final Logger log = LoggerFactory.getLogger(RespostaUsuarioConsumer.class);

    private final AgendamentoJPARepository agendamentoJPARepository;
    private final AntecipacaoConsultaProducer antecipacaoConsultaProducer;
    private final AgendamentoUseCase agendamentoUseCase;
    
    public RespostaUsuarioConsumer(AgendamentoJPARepository agendamentoJPARepository, AntecipacaoConsultaProducer antecipacaoConsultaProducer, AgendamentoUseCase agendamentoUseCase) {
        this.agendamentoJPARepository = agendamentoJPARepository;
        this.antecipacaoConsultaProducer = antecipacaoConsultaProducer;
        this.agendamentoUseCase = agendamentoUseCase;
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE_RESPOSTA_USUARIO)
    @Transactional
    public void processarResposta(EventoRespostaUsuario event) {
        // Validação de segurança: ignorar mensagens nulas
        if (event == null || event.identificador() == null) {
            log.warn("[RespostaUsuario] Mensagem nula ou sem identificador recebida. Descartando.");
            return;
        }

        log.info("[RabbitMQ] RespostaUsuario recebida identificador={} resposta={} canal={} dataRecebimento={}",
                event.identificador(), event.resposta(), event.canal(), event.dataRecebimento());

        String token = event.identificador();

        // Busca no banco por tokenUUID
        Optional<AgendamentoEntity> opt = agendamentoJPARepository.findByTokenUUID(token);
        if (opt.isEmpty()) {
            log.warn("[RespostaUsuario] Token não encontrado no banco: {}", token);
            return;
        }

        AgendamentoEntity agendamento = opt.get();
        String resposta = event.resposta();

        try {
            if ("CONFIRMAR".equalsIgnoreCase(resposta) || "SIM".equalsIgnoreCase(resposta)) {
                agendamento.setStatus(StatusAgendamentoEnum.CONFIRMADO_PACIENTE);
            } else if ("CANCELAR".equalsIgnoreCase(resposta) || "NAO".equalsIgnoreCase(resposta) || "NÃO".equalsIgnoreCase(resposta)) {
                agendamento.setStatus(StatusAgendamentoEnum.CANCELADO);
            } else {
                log.info("[RespostaUsuario] Resposta desconhecida='{}'. Nenhuma ação aplicada.", resposta);
                return;
            }

            agendamentoUseCase.execute(AgendamentoEntityPresenters.toInput(agendamento));

        } catch (Exception e) {
            log.error("[RespostaUsuario] Erro ao atualizar status para token={}", token, e);
        }
    }
}
