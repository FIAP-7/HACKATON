package br.com.sus.ms_processamento.infrastructure.api.listener;

import br.com.sus.ms_processamento.infrastructure.api.event.EventoRespostaUsuario;
import br.com.sus.ms_processamento.application.usecase.agendamento.AgendamentoUseCase;
import br.com.sus.ms_processamento.domain.model.StatusAgendamentoEnum;
import br.com.sus.ms_processamento.infrastructure.config.RabbitMqConfig;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoPacienteEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.repository.AgendamentoPacienteJPARepository;
import br.com.sus.ms_processamento.infrastructure.presenters.AgendamentoEntityPresenters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class AntecipacaoConsumer {

    private static final Logger log = LoggerFactory.getLogger(AntecipacaoConsumer.class);

    private final AgendamentoPacienteJPARepository agendamentoPacienteJPARepository;
    private final AgendamentoUseCase agendamentoUseCase;

    public AntecipacaoConsumer(AgendamentoPacienteJPARepository agendamentoPacienteJPARepository,
                               AgendamentoUseCase agendamentoUseCase) {
        this.agendamentoPacienteJPARepository = agendamentoPacienteJPARepository;
        this.agendamentoUseCase = agendamentoUseCase;
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE_ANTECIPACAO_USUARIO)
    @Transactional
    public void processarAntecipacao(EventoRespostaUsuario event) {
        if (event == null || event.identificador() == null) {
            log.warn("[AntecipacaoConsumer] Evento nulo ou sem token recebido. Descartando.");
            return;
        }

        String token = event.identificador();
        log.info("[RabbitMQ] AntecipacaoConsumer recebida token={} resposta={} canal={} dataRecebimento={}",
                token, event.resposta(), event.canal(), event.dataRecebimento());

        Optional<AgendamentoPacienteEntity> opt = agendamentoPacienteJPARepository.findByToken(token);
        if (opt.isEmpty()) {
            log.warn("[AntecipacaoConsumer] Token={} não encontrado em agendamento_paciente. Ignorando.", token);
            return;
        }

        AgendamentoPacienteEntity ap = opt.get();
        AgendamentoEntity agendamento = ap.getAgendamento();

        try {
            String resposta = event.resposta();

            if ("ACEITAR".equalsIgnoreCase(resposta)) {
                if (StatusAgendamentoEnum.REALOCADO.equals(agendamento.getStatus())) {
                    log.error("[AntecipacaoConsumer] Agendamento id={} já está em status REALOCADO. Não permitir antecipação.", agendamento.getId());
                    throw new IllegalStateException("Agendamento já foi realocado. Antecipação não permitida.");
                }

                agendamento.setStatus(StatusAgendamentoEnum.REALOCADO);
                ap.setStatus(StatusAgendamentoEnum.REALOCADO.toString());

                List<AgendamentoPacienteEntity> outrosTokens = agendamentoPacienteJPARepository.findByAgendamento_Id(agendamento.getId());
                for (AgendamentoPacienteEntity outro : outrosTokens) {
                    if (!outro.getId().equals(ap.getId())) {
                        outro.setToken("");
                        outro.setStatus(StatusAgendamentoEnum.CANCELADO.toString());
                        agendamentoPacienteJPARepository.save(outro);
                        log.info("[AntecipacaoConsumer] Token invalidado para outro registro: id={}", outro.getId());
                    }
                }

                log.info("[AntecipacaoConsumer] Antecipação ACEITA - agendamento id={} status={}", agendamento.getId(), agendamento.getStatus());

            } else if ("MANTER".equalsIgnoreCase(resposta)) {
                ap.setStatus(StatusAgendamentoEnum.CANCELADO.toString());
                log.info("[AntecipacaoConsumer] Antecipação RECUSADA - agendamento id={} mantém horário original", agendamento.getId());

            } else {
                log.info("[AntecipacaoConsumer] Resposta desconhecida='{}'. Nenhuma ação aplicada.", resposta);
                return;
            }

            ap.setToken("");
            agendamentoPacienteJPARepository.save(ap);

            log.info("[AntecipacaoConsumer] Finalizado processamento - agendamento id={} status={} (token removido)", 
                    agendamento.getId(), agendamento.getStatus());
            agendamentoUseCase.execute(AgendamentoEntityPresenters.toInput(agendamento));

        } catch (IllegalStateException e) {
            log.error("[AntecipacaoConsumer] Validação falhou para token={}: {}", token, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("[AntecipacaoConsumer] Erro ao processar antecipacao para token={}", token, e);
            throw e;
        }
    }
}

