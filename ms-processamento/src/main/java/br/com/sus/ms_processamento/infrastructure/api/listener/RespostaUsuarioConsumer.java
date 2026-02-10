package br.com.sus.ms_processamento.infrastructure.api.listener;

import br.com.sus.ms_processamento.infrastructure.api.event.EventoRespostaUsuario;
import br.com.sus.ms_processamento.infrastructure.api.event.AntecipacaoConsultaEvent;
import br.com.sus.ms_processamento.infrastructure.api.producer.AntecipacaoConsultaProducer;
import br.com.sus.ms_processamento.infrastructure.config.RabbitMqConfig;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.repository.AgendamentoJPARepository;
import br.com.sus.ms_processamento.domain.model.StatusAgendamentoEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RespostaUsuarioConsumer {

    private static final Logger log = LoggerFactory.getLogger(RespostaUsuarioConsumer.class);

    private final AgendamentoJPARepository agendamentoJPARepository;
    private final AntecipacaoConsultaProducer antecipacaoConsultaProducer;
    
    public RespostaUsuarioConsumer(AgendamentoJPARepository agendamentoJPARepository, AntecipacaoConsultaProducer antecipacaoConsultaProducer) {
        this.agendamentoJPARepository = agendamentoJPARepository;
        this.antecipacaoConsultaProducer = antecipacaoConsultaProducer;
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

            // remove token after successful status update to prevent reuse
            agendamento.setTokenUUID(null);
            agendamentoJPARepository.save(agendamento);
            log.info("[RespostaUsuario] Agendamento idExterno={} atualizado para status={} e token removido", agendamento.getIdExterno(), agendamento.getStatus());

            // If cancellation, try to antecipate: check if still more than 24h until dataLimiteConsulta
            if (StatusAgendamentoEnum.CANCELADO.equals(agendamento.getStatus())) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime threshold = now.plusHours(24);

                if (agendamento.getDataLimiteConsulta() != null && agendamento.getDataLimiteConsulta().isAfter(threshold)) {
                    // find up to 5 confirmed appointments same specialty + unit with dataLimiteConsulta after threshold
                    List<AgendamentoEntity> candidatos = agendamentoJPARepository.findTop5ByStatusAndEspecialidadeAndUnidadeIdAndDataLimiteConsultaAfter(
                            StatusAgendamentoEnum.CONFIRMADO_PACIENTE,
                            agendamento.getEspecialidade(),
                            agendamento.getUnidadeId(),
                            threshold);

                    for (AgendamentoEntity candidato : candidatos) {
                        String novoToken = UUID.randomUUID().toString();
                        candidato.setTokenUUID(novoToken);
                        agendamentoJPARepository.save(candidato);

                        AntecipacaoConsultaEvent evento = AntecipacaoConsultaEvent.from(agendamento, candidato, novoToken);
                        antecipacaoConsultaProducer.enviarAntecipacao(evento);
                        log.info("[RespostaUsuario] Enviado antecipacao para agendamento nome={} email={}", candidato.getPacienteNome(), candidato.getPacienteEmail());
                    }
                    log.info("[RespostaUsuario] {} candidatos encontrados para antecipacao", candidatos.size());
                } else {
                    log.info("[RespostaUsuario] Agendamento cancelado nao possui margem de 24h para antecipacao");
                }
            }

        } catch (Exception e) {
            log.error("[RespostaUsuario] Erro ao atualizar status para token={}", token, e);
        }
    }
}
