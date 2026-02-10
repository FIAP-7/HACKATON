package br.com.sus.ms_processamento.infrastructure.gateway;

import br.com.sus.ms_processamento.application.gateway.IAgendamentoGateway;
import br.com.sus.ms_processamento.domain.model.Agendamento;
import br.com.sus.ms_processamento.domain.model.StatusAgendamentoEnum;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.repository.AgendamentoJPARepository;
import br.com.sus.ms_processamento.infrastructure.presenters.AgendamentoEntityPresenters;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AgendamentoGateway implements IAgendamentoGateway {

    private final AgendamentoJPARepository agendamentoJPARepository;

    public AgendamentoGateway(AgendamentoJPARepository agendamentoJPARepository) {
        this.agendamentoJPARepository = agendamentoJPARepository;
    }

    @Override
    public void enviarConfirmacao(Agendamento agendamento) {

    }

    @Override
    public void atualizarStatusAgendamento(UUID userId, StatusAgendamentoEnum statusAgendamento) {

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

    }

    @Override
    public Agendamento buscarAgendamento(UUID idAgendamento) {
        return null;
    }
}
