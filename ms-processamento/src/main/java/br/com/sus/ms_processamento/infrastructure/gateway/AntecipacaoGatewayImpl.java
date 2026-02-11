package br.com.sus.ms_processamento.infrastructure.gateway;

import br.com.sus.ms_processamento.application.gateway.IAntecipacaoGateway;
import br.com.sus.ms_processamento.domain.model.StatusAgendamentoEnum;
import br.com.sus.ms_processamento.domain.model.StatusAgendamentoPacienteEnum;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoPacienteEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.repository.AgendamentoPacienteJPARepository;
import br.com.sus.ms_processamento.infrastructure.persistence.repository.AgendamentoJPARepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class AntecipacaoGatewayImpl implements IAntecipacaoGateway {

    private static final Logger log = LoggerFactory.getLogger(AntecipacaoGatewayImpl.class);
    private static final String TOKEN_VAZIO = "";

    private final AgendamentoPacienteJPARepository agendamentoPacienteJPARepository;
    private final AgendamentoJPARepository agendamentoJPARepository;

    public AntecipacaoGatewayImpl(AgendamentoPacienteJPARepository agendamentoPacienteJPARepository,
                                  AgendamentoJPARepository agendamentoJPARepository) {
        this.agendamentoPacienteJPARepository = agendamentoPacienteJPARepository;
        this.agendamentoJPARepository = agendamentoJPARepository;
    }

    @Override
    @Transactional
    public void confirmarAntecipacao(String token) {
        Optional<AgendamentoPacienteEntity> agendamentoPacienteOpt = agendamentoPacienteJPARepository.findByToken(token);
        
        if (agendamentoPacienteOpt.isEmpty()) {
            log.warn("[AntecipacaoGateway] Token={} não encontrado em agendamento_paciente. Ignorando.", token);
            return;
        }

        AgendamentoPacienteEntity agendamentoPaciente = agendamentoPacienteOpt.get();
        AgendamentoEntity agendamento = agendamentoPaciente.getAgendamento();

        if (jaFoiRealocado(agendamento)) {
            marcarIndisponivel(agendamentoPaciente);
            log.info("[AntecipacaoGateway] Agendamento id={} já está em status REALOCADO. Antecipação recusada.", 
                    agendamento.getId());
            return;
        }

        realizarRelocacao(agendamento, agendamentoPaciente);
        marcarAgendamentoAnteriorComoAntecipar(agendamentoPaciente);
        invalidarOutrosTokens(agendamento, agendamentoPaciente);
        finalizarProcessamento(agendamentoPaciente);
        
        log.info("[AntecipacaoGateway] Antecipação CONFIRMADA - agendamento id={} status={}", 
                agendamento.getId(), agendamento.getStatus());
    }

    @Override
    @Transactional
    public void recusarAntecipacao(String token) {
        Optional<AgendamentoPacienteEntity> agendamentoPacienteOpt = agendamentoPacienteJPARepository.findByToken(token);
        
        if (agendamentoPacienteOpt.isEmpty()) {
            log.warn("[AntecipacaoGateway] Token={} não encontrado em agendamento_paciente. Ignorando.", token);
            return;
        }

        AgendamentoPacienteEntity agendamentoPaciente = agendamentoPacienteOpt.get();
        agendamentoPaciente.setStatus(StatusAgendamentoPacienteEnum.RECUSADO.toString());
        finalizarProcessamento(agendamentoPaciente);
        
        log.info("[AntecipacaoGateway] Antecipação RECUSADA - agendamento id={} mantém horário original", 
                agendamentoPaciente.getAgendamento().getId());
    }

    private boolean jaFoiRealocado(AgendamentoEntity agendamento) {
        return StatusAgendamentoEnum.REALOCADO.equals(agendamento.getStatus());
    }

    private void marcarIndisponivel(AgendamentoPacienteEntity agendamentoPaciente) {
        agendamentoPaciente.setStatus(StatusAgendamentoPacienteEnum.INDISPONIVEL.toString());
        agendamentoPaciente.setToken(TOKEN_VAZIO);
        agendamentoPacienteJPARepository.save(agendamentoPaciente);
    }

    private void realizarRelocacao(AgendamentoEntity agendamento, AgendamentoPacienteEntity agendamentoPaciente) {
        agendamento.setStatus(StatusAgendamentoEnum.REALOCADO);
        agendamento.setPaciente(agendamentoPaciente.getPaciente());
        agendamentoPaciente.setStatus(StatusAgendamentoPacienteEnum.ANTECIPADO.toString());
        agendamentoJPARepository.save(agendamento);
    }

    private void marcarAgendamentoAnteriorComoAntecipar(AgendamentoPacienteEntity agendamentoPaciente) {
        AgendamentoEntity agendamento = agendamentoPaciente.getAgendamento();
        
        Optional<AgendamentoEntity> agendamentoAnteriorOpt = agendamentoJPARepository
                .findTopByPaciente_CpfAndEspecialidadeAndIdNotOrderByDataHoraAsc(
                        agendamentoPaciente.getPaciente().getCpf(), 
                        agendamento.getEspecialidade(), 
                        agendamento.getId());

        if (agendamentoAnteriorOpt.isPresent()) {
            AgendamentoEntity agendamentoAnterior = agendamentoAnteriorOpt.get();
            if (!StatusAgendamentoEnum.REALOCADO.equals(agendamentoAnterior.getStatus())) {
                agendamentoAnterior.setStatus(StatusAgendamentoEnum.ANTECIPAR);
                agendamentoJPARepository.save(agendamentoAnterior);
                log.info("[AntecipacaoGateway] Agendamento anterior id={} marcado como ANTECIPAR", 
                        agendamentoAnterior.getId());
            }
        } else {
            log.info("[AntecipacaoGateway] Nenhum agendamento anterior encontrado para paciente={}", 
                    agendamentoPaciente.getPaciente().getCpf());
        }
    }

    private void invalidarOutrosTokens(AgendamentoEntity agendamento, AgendamentoPacienteEntity agendamentoPacienteAtual) {
        List<AgendamentoPacienteEntity> outrosRegistros = agendamentoPacienteJPARepository.findByAgendamento_Id(agendamento.getId());
        
        outrosRegistros.stream()
                .filter(outro -> outro.getStatus().equals(StatusAgendamentoPacienteEnum.AGUARDANDO_ANTECIPACAO.toString()) )
                .forEach(outro -> {
                    outro.setToken(TOKEN_VAZIO);
                    outro.setStatus(StatusAgendamentoPacienteEnum.INDISPONIVEL.toString());
                    agendamentoPacienteJPARepository.save(outro);
                    log.info("[AntecipacaoGateway] Token invalidado para outro registro: id={}", outro.getId());
                });
    }

    private void finalizarProcessamento(AgendamentoPacienteEntity agendamentoPaciente) {
        agendamentoPaciente.setToken(TOKEN_VAZIO);
        agendamentoPacienteJPARepository.save(agendamentoPaciente);
        log.info("[AntecipacaoGateway] Finalizado processamento - agendamento id={} status={} (token removido)",
                agendamentoPaciente.getAgendamento().getId(),
                agendamentoPaciente.getAgendamento().getStatus());
    }
}
