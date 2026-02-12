package br.com.sus.ms_processamento.infrastructure.gateway;

import br.com.sus.ms_processamento.application.gateway.IAgendamentoGateway;
import br.com.sus.ms_processamento.domain.model.Agendamento;
import br.com.sus.ms_processamento.domain.model.Paciente;
import br.com.sus.ms_processamento.domain.model.StatusAgendamentoEnum;
import br.com.sus.ms_processamento.domain.model.StatusAgendamentoPacienteEnum;
import br.com.sus.ms_processamento.infrastructure.api.event.AntecipacaoConsultaEvent;
import br.com.sus.ms_processamento.infrastructure.api.event.AgendamentoEvent;
import br.com.sus.ms_processamento.infrastructure.api.event.ConfirmacaoConsultaEvent;
import br.com.sus.ms_processamento.infrastructure.api.producer.AntecipacaoConsultaProducer;
import br.com.sus.ms_processamento.infrastructure.api.producer.ConfirmacaoConsultaProducer;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoPacienteEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.PacienteEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.repository.AgendamentoJPARepository;
import br.com.sus.ms_processamento.infrastructure.persistence.repository.AgendamentoPacienteJPARepository;
import br.com.sus.ms_processamento.infrastructure.persistence.repository.PacienteJPARepository;
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
import java.util.Arrays;

@Component
public class AgendamentoGateway implements IAgendamentoGateway {

    private static final Logger log = LoggerFactory.getLogger(AgendamentoGateway.class);
    private static final int HORAS_MINIMA_ANTECIPACAO = 24;
    private static final int HORAS_BUSCA_CONSULTAS = 48;
    private static final int QUANTIDADE_AGENDAMENTOS_SELECIONADOS = 3;
    private static final int QUANTIDADE_CANDIDATOS_BUSCA = 15;

    private final AgendamentoJPARepository agendamentoJPARepository;
    private final AntecipacaoConsultaProducer antecipacaoConsultaProducer;
    private final AgendamentoPacienteJPARepository agendamentoPacienteJPARepository;
    private final PacienteJPARepository pacienteJPARepository;
    private final ConfirmacaoConsultaProducer confirmacaoConsultaProducer;

    public AgendamentoGateway(AgendamentoJPARepository agendamentoJPARepository, AntecipacaoConsultaProducer antecipacaoConsultaProducer, AgendamentoPacienteJPARepository agendamentoPacienteJPARepository, PacienteJPARepository pacienteJPARepository, ConfirmacaoConsultaProducer confirmacaoConsultaProducer) {
        this.agendamentoJPARepository = agendamentoJPARepository;
        this.antecipacaoConsultaProducer = antecipacaoConsultaProducer;
        this.agendamentoPacienteJPARepository = agendamentoPacienteJPARepository;
        this.pacienteJPARepository = pacienteJPARepository;
        this.confirmacaoConsultaProducer = confirmacaoConsultaProducer;
    }

    @Override
    public void enviarConfirmacao(Agendamento agendamento) {

        String tokenUUID = UUID.randomUUID().toString();

        Optional<AgendamentoEntity> agendamentoOpt = agendamentoJPARepository.findById(agendamento.getId());
        Optional<PacienteEntity> pacienteOpt = pacienteJPARepository.findById(agendamento.getPaciente().getCpf());

        if (agendamentoOpt.isEmpty() || pacienteOpt.isEmpty()) {
            log.warn("[PostgreSQL] Agendamento ou Paciente não encontrado. idAgendamento={}, cpfPaciente={}", agendamento.getId(), agendamento.getPaciente().getCpf());
            return;
        }

        PacienteEntity pacienteEntity = pacienteOpt.get();
        
        AgendamentoEntity agendamentoEntity = agendamentoOpt.get();
        agendamentoEntity.setStatus(StatusAgendamentoEnum.AGUARDANDO_CONFIRMACAO);
        agendamentoJPARepository.save(agendamentoEntity);

        log.info("[PostgreSQL] Agendamento salvo com sucesso. dado={}", agendamentoEntity.toString());

        AgendamentoPacienteEntity agendamentoPaciente = AgendamentoPacienteEntity.builder()
                .paciente(agendamentoEntity.getPaciente())
                .agendamento(agendamentoEntity)
                .dataRegistro(LocalDateTime.now())
                .status(StatusAgendamentoPacienteEnum.AGUARDANDO_CONFIRMACAO.name())
                .token(tokenUUID)
                .build();
                
        agendamentoPacienteJPARepository.save(agendamentoPaciente);

        log.info("[PostgreSQL] Registro agendamento_paciente salvo com sucesso.");

        AgendamentoEvent event = new AgendamentoEvent(
                agendamentoEntity.getIdExterno(),
                new AgendamentoEvent.Paciente(
                        pacienteEntity.getNome(),
                        pacienteEntity.getCpf(),
                        pacienteEntity.getTelefone(),
                        pacienteEntity.getEmail()
                ),
                new AgendamentoEvent.Consulta(
                        agendamentoEntity.getDataHora(),
                        agendamentoEntity.getMedico(),
                        agendamentoEntity.getEspecialidade(),
                        agendamentoEntity.getEndereco(),
                        agendamentoEntity.getLocalAtendimento(),
                        agendamentoEntity.getUnidadeId()
                ),
                LocalDateTime.now()
        );

        ConfirmacaoConsultaEvent confirmacaoEvent = ConfirmacaoConsultaEvent.from(event, tokenUUID);

        confirmacaoConsultaProducer.enviarConfirmacao(confirmacaoEvent);
        log.info("[RabbitMQ] Mensagem de confirmação enviada. idExterno={}, tokenUUID={}", agendamento.getIdExterno(), tokenUUID);
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

        if (!temMargemTempoParaAntecipacao(agendamento.getDataHora())) {
            log.info("[RespostaUsuario] Agendamento nao possui margem de 24h para antecipacao");
            return;
        }

        processarAntecipacaoAgendamento(agendamentoEntity, agendamento);
    }

    private boolean temMargemTempoParaAntecipacao(LocalDateTime dataHoraAgendamento) {
        if (dataHoraAgendamento == null) {
            return false;
        }
        LocalDateTime limiteMinimo = LocalDateTime.now().plusHours(HORAS_MINIMA_ANTECIPACAO);
        return dataHoraAgendamento.isAfter(limiteMinimo);
    }

    private void processarAntecipacaoAgendamento(AgendamentoEntity agendamentoEntity, Agendamento agendamentoDomain) {
        if(agendamentoDomain.getDataHora().isBefore(LocalDateTime.now().plusHours(HORAS_MINIMA_ANTECIPACAO))) {
            log.info("[RespostaUsuario] Agendamento idExterno={} não possui margem de 24h para antecipacao. DataHora={}", agendamentoDomain.getIdExterno(), agendamentoDomain.getDataHora());
            return;
        }

        List<AgendamentoEntity> candidatos = buscarCandidatos(
                agendamentoDomain.getEspecialidade(),
                agendamentoDomain.getUnidadeId(),
                agendamentoDomain.getDataHora().plusDays(1)
        );

        List<AgendamentoEntity> candidatosValidos = filtrarCandidatosJaOferecidos(
                agendamentoEntity.getId(),
                candidatos
        );

        enviarAntecipacaoParaCandidatos(agendamentoEntity, candidatosValidos);
        log.info("[RespostaUsuario] {} candidatos encontrados. {} selecionados para antecipacao", 
                 candidatos.size(), candidatosValidos.size());
    }

    private LocalDateTime calcularDataHoraBusca(LocalDateTime dataHoraAgendamento) {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime limiteMinimo24h = agora.plusHours(HORAS_MINIMA_ANTECIPACAO);
        LocalDateTime limiteMaximo48h = agora.plusHours(HORAS_BUSCA_CONSULTAS);

        return dataHoraAgendamento.isAfter(limiteMaximo48h) ? limiteMaximo48h : limiteMinimo24h;
    }

    private List<AgendamentoEntity> buscarCandidatos(String especialidade, String unidadeId, LocalDateTime dataHoraBusca) {
        List<StatusAgendamentoEnum> statusValidos = Arrays.asList(
                StatusAgendamentoEnum.PENDENTE,
                StatusAgendamentoEnum.AGUARDANDO_CONFIRMACAO,
                StatusAgendamentoEnum.CONFIRMADO_PACIENTE,
                StatusAgendamentoEnum.CONFIRMADO_AUTOMATICO,
                StatusAgendamentoEnum.REALOCADO
        );

        return agendamentoJPARepository.findTop15ByMultipleStatusAndEspecialidadeAndUnidadeIdAndDataHoraAfter(
                statusValidos,
                especialidade,
                unidadeId,
                dataHoraBusca
        );
    }

    private List<AgendamentoEntity> filtrarCandidatosJaOferecidos(UUID agendamentoId, List<AgendamentoEntity> candidatos) {
        List<UUID> agendamentosJaOferecidos = obterAgendamentosJaOferecidos(agendamentoId);

        return candidatos.stream()
                .filter(candidato -> !agendamentosJaOferecidos.contains(candidato.getId()))
                .limit(QUANTIDADE_AGENDAMENTOS_SELECIONADOS)
                .collect(Collectors.toList());
    }

    private List<UUID> obterAgendamentosJaOferecidos(UUID agendamentoId) {
        return agendamentoPacienteJPARepository.findByAgendamento_Id(agendamentoId)
                .stream()
                .map(ap -> ap.getAgendamento().getId())
                .distinct()
                .collect(Collectors.toList());
    }

    private void enviarAntecipacaoParaCandidatos(AgendamentoEntity agendamentoEntity, List<AgendamentoEntity> candidatos) {
        if(candidatos.isEmpty()) {
            log.info("[RespostaUsuario] Nenhum candidato encontrado para antecipacao do agendamento idExterno={}", agendamentoEntity.getIdExterno());
            return;
        }
        for (AgendamentoEntity candidato : candidatos) {
            enviarAntecipacaoParaCandidato(agendamentoEntity, candidato);
        }
        agendamentoEntity.setStatus(StatusAgendamentoEnum.AGUARDANDO_ANTECIPACAO);
        agendamentoJPARepository.save(agendamentoEntity);
    }

    private void enviarAntecipacaoParaCandidato(AgendamentoEntity agendamentoEntity, AgendamentoEntity candidato) {
        String novoToken = UUID.randomUUID().toString();

        AgendamentoPacienteEntity agendamentoPaciente = obterOuCriarAgendamentoPaciente(
                agendamentoEntity,
                candidato,
                novoToken
        );

        agendamentoPacienteJPARepository.save(agendamentoPaciente);

        AntecipacaoConsultaEvent evento = AntecipacaoConsultaEvent.from(agendamentoEntity, candidato, novoToken);
        antecipacaoConsultaProducer.enviarAntecipacao(evento);
        log.info("[RespostaUsuario] Enviado antecipacao para agendamento nome={} email={}", 
                 candidato.getPaciente().getNome(), candidato.getPaciente().getEmail());
    }

    private AgendamentoPacienteEntity obterOuCriarAgendamentoPaciente(
            AgendamentoEntity agendamentoEntity,
            AgendamentoEntity candidato,
            String token) {
        
        Optional<AgendamentoPacienteEntity> existente = agendamentoPacienteJPARepository
                .findByPaciente_CpfAndAgendamento_Id(candidato.getPaciente().getCpf(), candidato.getId());

        if (existente.isPresent()) {
            AgendamentoPacienteEntity ap = existente.get();
            ap.setToken(token);
            ap.setStatus(StatusAgendamentoPacienteEnum.AGUARDANDO_ANTECIPACAO.name());
            return ap;
        }

        return criarNovoAgendamentoPaciente(agendamentoEntity, candidato, token);
    }

    private AgendamentoPacienteEntity criarNovoAgendamentoPaciente(
            AgendamentoEntity agendamentoEntity,
            AgendamentoEntity candidato,
            String token) {
        
        AgendamentoPacienteEntity agendamentoPaciente = new AgendamentoPacienteEntity();
        agendamentoPaciente.setAgendamento(agendamentoEntity);
        agendamentoPaciente.setPaciente(candidato.getPaciente());
        agendamentoPaciente.setDataRegistro(LocalDateTime.now());
        agendamentoPaciente.setStatus(StatusAgendamentoPacienteEnum.AGUARDANDO_ANTECIPACAO.name());
        agendamentoPaciente.setToken(token);
        
        return agendamentoPaciente;
    }

    @Override
    public Agendamento buscarAgendamento(UUID idAgendamento) {
        return null;
    }
}
