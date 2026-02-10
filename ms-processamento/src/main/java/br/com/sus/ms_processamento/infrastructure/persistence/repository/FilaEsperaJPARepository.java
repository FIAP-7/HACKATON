package br.com.sus.ms_processamento.infrastructure.persistence.repository;

import br.com.sus.ms_processamento.infrastructure.persistence.entity.FilaEsperaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FilaEsperaJPARepository extends JpaRepository<FilaEsperaEntity, UUID> {

    List<FilaEsperaEntity> findTop3ByEspecialidadeAndUnidadeIdOrderByDataSolicitacao(String especialidade, String unidadeId);

}
