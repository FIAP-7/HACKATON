package br.com.sus.ms_processamento.infrastructure.gateway;

import br.com.sus.ms_processamento.application.gateway.IAgendamentoGateway;
import br.com.sus.ms_processamento.domain.model.Agendamento;
import br.com.sus.ms_processamento.domain.model.StatusAgendamentoEnum;
import br.com.sus.ms_processamento.infrastructure.api.event.AntecipacaoConsultaEvent;
import br.com.sus.ms_processamento.infrastructure.api.producer.AntecipacaoConsultaProducer;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoPacienteEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.repository.AgendamentoJPARepository;
import br.com.sus.ms_processamento.infrastructure.persistence.repository.AgendamentoPacienteJPARepository;
import br.com.sus.ms_processamento.infrastructure.presenters.AgendamentoEntityPresenters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Comparator;
import java.util.stream.Collectors;

@Component
public class AgendamentoGateway implements IAgendamentoGateway {

    private static final Logger log = LoggerFactory.getLogger(AgendamentoGateway.class);

    private final AgendamentoJPARepository agendamentoJPARepository;
    private final AntecipacaoConsultaProducer antecipacaoConsultaProducer;
    private final AgendamentoPacienteJPARepository agendamentoPacienteJPARepository;

    public AgendamentoGateway(AgendamentoJPARepository agendamentoJPARepository, AntecipacaoConsultaProducer antecipacaoConsultaProducer, AgendamentoPacienteJPARepository agendamentoPacienteJPARepository) {
        this.agendamentoJPARepository = agendamentoJPARepository;
        this.antecipacaoConsultaProducer = antecipacaoConsultaProducer;
        this.agendamentoPacienteJPARepository = agendamentoPacienteJPARepository;
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
        agendamentoJPARepository.save(agendamentoEntity);
    }

    @Override
    public void realocarAgendamento(Agendamento agendamento) {
        AgendamentoEntity agendamentoEntity = AgendamentoEntityPresenters.toEntity(agendamento);

        agendamentoJPARepository.save(agendamentoEntity);
        log.info("[RespostaUsuario] Agendamento idExterno={} atualizado para status={}", agendamento.getIdExterno(), agendamento.getStatus());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold24 = now.plusHours(24);

        if (agendamento.getDataLimiteConsulta() != null && agendamento.getDataLimiteConsulta().isAfter(threshold24)) {

            LocalDateTime threshold48 = now.plusHours(48);

            LocalDateTime buscarConsultas = agendamento.getDataLimiteConsulta().isAfter(threshold48) ? threshold48 : threshold24;

            List<AgendamentoEntity> candidatos = agendamentoJPARepository.findTop5ByStatusAndEspecialidadeAndUnidadeIdAndDataLimiteConsultaAfter(
                    StatusAgendamentoEnum.CONFIRMADO_PACIENTE,
                    agendamento.getEspecialidade(),
                    agendamento.getUnidadeId(),
                    buscarConsultas);

            List<AgendamentoEntity> selecionados = candidatos.stream()
                    .filter(c -> c.getDataLimiteConsulta() != null && c.getDataLimiteConsulta().isAfter(buscarConsultas))
                    .sorted(Comparator.comparing(AgendamentoEntity::getDataLimiteConsulta))
                    .limit(3)
                    .collect(Collectors.toList());

            for (AgendamentoEntity candidato : selecionados) {
                String novoToken = UUID.randomUUID().toString();

                Optional<AgendamentoPacienteEntity> existente = agendamentoPacienteJPARepository
                        .findByPaciente_CpfAndAgendamento_Id(candidato.getPaciente().getCpf(), candidato.getId());

                AgendamentoPacienteEntity ap;
                if (existente.isPresent()) {
                    ap = existente.get();
                    ap.setToken(novoToken);
                    ap.setStatus(StatusAgendamentoEnum.ANTECIPAR.name());
                } else {
                    ap = new AgendamentoPacienteEntity();
                    ap.setAgendamento(candidato);
                    ap.setPaciente(candidato.getPaciente());
                    ap.setDataRegistro(LocalDateTime.now());
                    ap.setStatus(StatusAgendamentoEnum.ANTECIPAR.name());
                    ap.setToken(novoToken);
                }

                agendamentoPacienteJPARepository.save(ap);

                AntecipacaoConsultaEvent evento = AntecipacaoConsultaEvent.from(agendamentoEntity, candidato, novoToken);
                antecipacaoConsultaProducer.enviarAntecipacao(evento);
                log.info("[RespostaUsuario] Enviado antecipacao para agendamento nome={} email={}", candidato.getPaciente().getNome(), candidato.getPaciente().getEmail());
            }
            log.info("[RespostaUsuario] {} candidatos encontrados para antecipacao (selecionados={})", candidatos.size(), selecionados.size());
        } else {
            log.info("[RespostaUsuario] Agendamento nao possui margem de 24h para antecipacao");
        }
    }

    @Override
    public Agendamento buscarAgendamento(UUID idAgendamento) {
        return null;
    }
}
