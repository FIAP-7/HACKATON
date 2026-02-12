package br.com.sus.ms_processamento.infrastructure.persistence.repository;

import br.com.sus.ms_processamento.domain.model.StatusAgendamentoEnum;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    List<AgendamentoEntity> findByStatusAndDataHoraBetween(StatusAgendamentoEnum statusAgendamentoEnum, LocalDateTime start, LocalDateTime end);

	List<AgendamentoEntity> findByStatus(StatusAgendamentoEnum status);

	@Query("SELECT a FROM AgendamentoEntity a WHERE a.status IN :status AND a.especialidade = :especialidade AND a.unidadeId = :unidadeId AND a.dataHora > :dataHora ORDER BY a.dataHora ASC LIMIT 5")
	List<AgendamentoEntity> findTop3ByMultipleStatusAndEspecialidadeAndUnidadeIdAndDataHoraAfter(
			@Param("status") List<StatusAgendamentoEnum> status,
			@Param("especialidade") String especialidade,
			@Param("unidadeId") String unidadeId,
			@Param("dataHora") LocalDateTime dataHora);
	@Query("SELECT a FROM AgendamentoEntity a WHERE a.status IN :status AND a.especialidade = :especialidade AND a.unidadeId = :unidadeId AND a.dataHora > :dataHora ORDER BY a.dataHora ASC LIMIT 15")
	List<AgendamentoEntity> findTop15ByMultipleStatusAndEspecialidadeAndUnidadeIdAndDataHoraAfter(
			@Param("status") List<StatusAgendamentoEnum> status,
			@Param("especialidade") String especialidade,
			@Param("unidadeId") String unidadeId,
			@Param("dataHora") LocalDateTime dataHora);}
