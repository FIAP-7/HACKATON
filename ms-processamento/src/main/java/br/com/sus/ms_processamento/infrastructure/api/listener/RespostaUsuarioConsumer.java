package br.com.sus.ms_processamento.infrastructure.api.listener;

import br.com.sus.ms_processamento.application.usecase.agendamento.AgendamentoUseCase;
import br.com.sus.ms_processamento.infrastructure.api.event.EventoRespostaUsuario;
import br.com.sus.ms_processamento.infrastructure.config.RabbitMqConfig;
import br.com.sus.ms_processamento.infrastructure.persistence.repository.AgendamentoPacienteJPARepository;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoPacienteEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoEntity;
import br.com.sus.ms_processamento.domain.model.StatusAgendamentoEnum;
import br.com.sus.ms_processamento.domain.model.StatusAgendamentoPacienteEnum;
import br.com.sus.ms_processamento.domain.model.RespostaPacienteEnum;
import java.util.Optional;

import br.com.sus.ms_processamento.infrastructure.presenters.AgendamentoEntityPresenters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RespostaUsuarioConsumer {

    private static final Logger log = LoggerFactory.getLogger(RespostaUsuarioConsumer.class);

    private final AgendamentoPacienteJPARepository agendamentoPacienteJPARepository;
    private final AgendamentoUseCase agendamentoUseCase;
    
    public RespostaUsuarioConsumer(AgendamentoPacienteJPARepository agendamentoPacienteJPARepository,
                                   AgendamentoUseCase agendamentoUseCase) {
        this.agendamentoPacienteJPARepository = agendamentoPacienteJPARepository;
        this.agendamentoUseCase = agendamentoUseCase;
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE_RESPOSTA_USUARIO)
    @Transactional
    public void processarResposta(EventoRespostaUsuario event) {
        if (event == null || event.identificador() == null) {
            log.warn("[RespostaUsuario] Mensagem nula ou sem identificador recebida. Descartando.");
            return;
        }

        log.info("[RabbitMQ] RespostaUsuario recebida identificador={} resposta={} canal={} dataRecebimento={}",
                event.identificador(), event.resposta(), event.canal(), event.dataRecebimento());

        String token = event.identificador();

        Optional<AgendamentoPacienteEntity> opt = agendamentoPacienteJPARepository.findByToken(token);
        if (opt.isEmpty()) {
            log.warn("[RespostaUsuario] Token={} não encontrado em agendamento_paciente. Ignorando.", token);
            return;
        }

        AgendamentoPacienteEntity ap = opt.get();
        AgendamentoEntity agendamento = ap.getAgendamento();
        String resposta = event.resposta();

        try {
            if (RespostaPacienteEnum.CONFIRMAR.name().equalsIgnoreCase(resposta) ) {
                agendamento.setStatus(StatusAgendamentoEnum.CONFIRMADO_PACIENTE);
                ap.setStatus(StatusAgendamentoPacienteEnum.CONFIRMADO_PACIENTE.toString());
            } else if (RespostaPacienteEnum.CANCELAR.name().equalsIgnoreCase(resposta) || "NAO".equalsIgnoreCase(resposta)) {
                ap.setStatus(StatusAgendamentoPacienteEnum.CANCELADO.toString());
                agendamento.setStatus(StatusAgendamentoEnum.CANCELADO);
            } else {
                log.info("[RespostaUsuario] Resposta desconhecida='{}'. Nenhuma ação aplicada.", resposta);
                return;
            }
            ap.setToken("");
            agendamentoPacienteJPARepository.save(ap);

            log.info("[RespostaUsuario] Atualizado agendamento id={} status={} (token removido)", agendamento.getId(), agendamento.getStatus());
            agendamentoUseCase.execute(AgendamentoEntityPresenters.toInput(agendamento));
        } catch (Exception e) {
            log.error("[RespostaUsuario] Erro ao processar resposta para token={}", token, e);
        }
    }
}
