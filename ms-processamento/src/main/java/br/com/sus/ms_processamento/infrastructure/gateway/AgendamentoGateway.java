package br.com.sus.ms_processamento.infrastructure.gateway;

import br.com.sus.ms_processamento.application.gateway.IAgendamentoGateway;
import br.com.sus.ms_processamento.domain.model.Agendamento;
import br.com.sus.ms_processamento.domain.model.StatusAgendamentoEnum;
import br.com.sus.ms_processamento.infrastructure.api.event.AntecipacaoConsultaEvent;
import br.com.sus.ms_processamento.infrastructure.api.producer.AntecipacaoConsultaProducer;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.repository.AgendamentoJPARepository;
import br.com.sus.ms_processamento.infrastructure.presenters.AgendamentoEntityPresenters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AgendamentoGateway implements IAgendamentoGateway {

    private static final Logger log = LoggerFactory.getLogger(AgendamentoGateway.class);

    private final AgendamentoJPARepository agendamentoJPARepository;
    private final AntecipacaoConsultaProducer antecipacaoConsultaProducer;

    public AgendamentoGateway(AgendamentoJPARepository agendamentoJPARepository, AntecipacaoConsultaProducer antecipacaoConsultaProducer) {
        this.agendamentoJPARepository = agendamentoJPARepository;
        this.antecipacaoConsultaProducer = antecipacaoConsultaProducer;
    }

    @Override
    public void enviarConfirmacao(Agendamento agendamento) {

    }

    @Override
    public void atualizarStatusAgendamento(UUID userId, StatusAgendamentoEnum statusAgendamento) {
        Optional<AgendamentoEntity> agendamento = agendamentoJPARepository.findById(userId);

        if (agendamento.isPresent()) {
            AgendamentoEntity agendamentoEntity = agendamento.get();

            agendamentoEntity.setStatus(statusAgendamento);

            agendamentoJPARepository.save(agendamentoEntity);
        }
    }

    @Override
    public void salvar(Agendamento agendamento) {
        AgendamentoEntity agendamentoEntity = AgendamentoEntityPresenters.toEntity(agendamento);

        // Preserve existing tokenUUID if present in DB (created earlier by consumer)
        if (agendamento.getId() != null) {
            agendamentoJPARepository.findById(agendamento.getId()).ifPresent(existing -> agendamentoEntity.setTokenUUID(existing.getTokenUUID()));
        } else if (agendamento.getIdExterno() != null) {
            agendamentoJPARepository.findByIdExterno(agendamento.getIdExterno()).ifPresent(existing -> agendamentoEntity.setTokenUUID(existing.getTokenUUID()));
        }

        agendamentoJPARepository.save(agendamentoEntity);
    }

    @Override
    public void realocarAgendamento(Agendamento agendamento) {
        AgendamentoEntity agendamentoEntity = AgendamentoEntityPresenters.toEntity(agendamento);

        // remove token after successful status update to prevent reuse
        agendamentoEntity.setTokenUUID(null);
        agendamentoJPARepository.save(agendamentoEntity);
        log.info("[RespostaUsuario] Agendamento idExterno={} atualizado para status={} e token removido", agendamento.getIdExterno(), agendamento.getStatus());

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

                AntecipacaoConsultaEvent evento = AntecipacaoConsultaEvent.from(agendamentoEntity, candidato, novoToken);
                antecipacaoConsultaProducer.enviarAntecipacao(evento);
                log.info("[RespostaUsuario] Enviado antecipacao para agendamento nome={} email={}", candidato.getPacienteNome(), candidato.getPacienteEmail());
            }
            log.info("[RespostaUsuario] {} candidatos encontrados para antecipacao", candidatos.size());
        } else {
            log.info("[RespostaUsuario] Agendamento cancelado nao possui margem de 24h para antecipacao");
        }
    }

    @Override
    public Agendamento buscarAgendamento(UUID idAgendamento) {
        return null;
    }
}
