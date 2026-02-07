package br.com.sus.ms_processamento.infrastructure.persistence.repository;

import br.com.sus.ms_processamento.domain.model.StatusReagendamentoEnum;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.ReagendamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReagendamentoJPARepository extends JpaRepository<ReagendamentoEntity, UUID> {

    long countAllByAgendamento_IdAndStatus(UUID agendamentoId, StatusReagendamentoEnum status);

}
