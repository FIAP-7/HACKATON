package br.com.sus.ms_processamento.infrastructure.persistence.repository;

import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AgendamentoJPARepository extends JpaRepository<AgendamentoEntity, UUID> {
}
