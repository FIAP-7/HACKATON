package br.com.sus.ms_processamento.infrastructure.presenters;

import br.com.sus.ms_processamento.domain.model.FilaEspera;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.FilaEsperaEntity;

public class FilaEsperaEntityPresenters {

    public static FilaEsperaEntity toEntity(FilaEspera filaEspera){
        return FilaEsperaEntity.builder()
                .id(filaEspera.getId())
                .pacienteNome(filaEspera.getPacienteNome())
                .pacienteTelefone(filaEspera.getPacienteTelefone())
                .especialidade(filaEspera.getEspecialidade())
                .unidadeSaude(filaEspera.getUnidadeSaude())
                .dataSolicitacao(filaEspera.getDataSolicitacao())
                .build();
    }

}
