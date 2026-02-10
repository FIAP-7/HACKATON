package br.com.sus.ms_processamento.infrastructure.persistence.repository;

import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoPacienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;

@Repository
public interface AgendamentoPacienteJPARepository extends JpaRepository<AgendamentoPacienteEntity, UUID> {
	Optional<AgendamentoPacienteEntity> findByToken(String token);
	Optional<AgendamentoPacienteEntity> findByPaciente_CpfAndAgendamento_Id(String pacienteCpf, UUID agendamentoId);
}
