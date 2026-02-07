package br.com.sus.ms_processamento.infrastructure.gateway;

import br.com.sus.ms_processamento.application.gateway.IReagendamentoGateway;
import br.com.sus.ms_processamento.domain.model.Agendamento;
import br.com.sus.ms_processamento.domain.model.Reagendamento;
import br.com.sus.ms_processamento.domain.model.StatusReagendamentoEnum;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.FilaEsperaEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.ReagendamentoEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.repository.FilaEsperaJPARepository;
import br.com.sus.ms_processamento.infrastructure.persistence.repository.ReagendamentoJPARepository;
import br.com.sus.ms_processamento.infrastructure.presenters.ReagendamentoEntityPresenters;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Component
public class ReagendamentoGateway implements IReagendamentoGateway {

    private final AgendamentoGateway agendamentoGateway;
    private final FilaEsperaJPARepository filaEsperaJPARepository;
    private final ReagendamentoJPARepository reagendamentoJPARepository;

    public ReagendamentoGateway(AgendamentoGateway agendamentoGateway, FilaEsperaJPARepository filaEsperaJPARepository, ReagendamentoJPARepository reagendamentoJPARepository) {
        this.agendamentoGateway = agendamentoGateway;
        this.filaEsperaJPARepository = filaEsperaJPARepository;
        this.reagendamentoJPARepository = reagendamentoJPARepository;
    }

    @Override
    public List<Reagendamento> buscarDisponivelParaReagendamento(Reagendamento reagendamento) {
        Agendamento agendamento = agendamentoGateway.buscarAgendamento(reagendamento.getIdAgendamento());

        List<FilaEsperaEntity> filaEsperaEntityList = filaEsperaJPARepository.findTop3ByEspecialidadeAndUnidadeSaudeOrderByDataSolicitacao(agendamento.getEspecialidade(), agendamento.getUnidadeSaude());

        return filaEsperaEntityList.stream()
                .map(filaEsperaEntity -> toReagendamento(filaEsperaEntity, reagendamento.getId(), reagendamento.getIdAgendamento()))
                .toList();
    }

    @Override
    public void enviarOpcaoReagendamento(List<Reagendamento> reagendamento) {

        reagendamento.forEach(reagendamentoEntity -> {
            //Enviar para a fila de mensageria
        });

    }

    @Override
    public Agendamento buscarAgendamentoPorId(UUID id) {
        return agendamentoGateway.buscarAgendamento(id);
    }

    @Override
    public Reagendamento buscarReagendamentoPorId(UUID id) {
        Optional<ReagendamentoEntity> reagendamentoEntityOptional = reagendamentoJPARepository.findById(id);

        return reagendamentoEntityOptional.map(ReagendamentoEntityPresenters::toDomain).orElse(null);
    }

    @Override
    public Reagendamento buscarReagendamentoPorAgendamentoEFila(UUID idAgendamento, UUID idFila) {
        return null;
    }

    @Override
    public void enviarInformativoJaAlocado(Reagendamento reagendamento) {
        //Enviar para a fila de mensageria
    }

    @Override
    public void enviarInformativoConfirmado(Reagendamento reagendamento) {
        //Enviar para a fila de mensageria
    }

    @Override
    public void atualizarStatusReagendamento(UUID idReagendamento, StatusReagendamentoEnum statusReagendamentoEnum) {

    }

    @Override
    public Reagendamento salvar(Reagendamento reagendamento) {
        ReagendamentoEntity entity = ReagendamentoEntityPresenters.toEntity(reagendamento);

        return ReagendamentoEntityPresenters.toDomain(reagendamentoJPARepository.save(entity));
    }

    @Override
    public List<Reagendamento> salvar(List<Reagendamento> reagendamentos) {
        List<ReagendamentoEntity> listEntity = reagendamentos.stream().map(ReagendamentoEntityPresenters::toEntity).toList();

        List<ReagendamentoEntity> reagendamentoEntities = reagendamentoJPARepository.saveAll(listEntity);

        return reagendamentoEntities.stream().map(ReagendamentoEntityPresenters::toDomain).toList();
    }

    @Override
    public boolean possuiReagendamentoPendenteResposta(UUID idAgendamento) {
        return reagendamentoJPARepository.countAllByAgendamento_IdAndStatus(idAgendamento, StatusReagendamentoEnum.PENDENTE) > 0;
    }

    private Reagendamento toReagendamento(FilaEsperaEntity filaEsperaEntity, UUID id, UUID idAgendamento) {
        Reagendamento reagendamento = new Reagendamento();

        reagendamento.setId(filaEsperaEntity.getId());
        reagendamento.setIdAgendamento(idAgendamento);
        reagendamento.setId(id);

        return reagendamento;
    }
}
