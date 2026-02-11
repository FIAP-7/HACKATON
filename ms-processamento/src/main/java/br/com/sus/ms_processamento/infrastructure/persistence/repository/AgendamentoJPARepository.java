package br.com.sus.ms_processamento.infrastructure.persistence.repository;

import br.com.sus.ms_processamento.domain.model.StatusAgendamentoEnum;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgendamentoJPARepository extends JpaRepository<AgendamentoEntity, UUID> {

	Optional<AgendamentoEntity> findByIdExterno(String idExterno);

	List<AgendamentoEntity> findTop5ByStatusAndEspecialidadeAndUnidadeIdAndDataLimiteConsultaAfter(
			StatusAgendamentoEnum status,
			String especialidade,
			String unidadeId,
			LocalDateTime dataLimiteConsulta);

	    Optional<AgendamentoEntity> findTopByPaciente_CpfAndEspecialidadeAndIdNotOrderByDataHoraAsc(
		    String cpf,
		    String especialidade,
		    UUID idNot);

}
