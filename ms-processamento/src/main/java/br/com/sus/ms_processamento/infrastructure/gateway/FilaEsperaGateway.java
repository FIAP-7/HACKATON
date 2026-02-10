package br.com.sus.ms_processamento.infrastructure.gateway;

import br.com.sus.ms_processamento.application.gateway.IFilaEsperaGateway;
import br.com.sus.ms_processamento.domain.model.FilaEspera;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.FilaEsperaEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.repository.FilaEsperaJPARepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FilaEsperaGateway implements IFilaEsperaGateway {

    private final FilaEsperaJPARepository filaEsperaJPARepository;

    public FilaEsperaGateway(FilaEsperaJPARepository filaEsperaJPARepository) {
        this.filaEsperaJPARepository = filaEsperaJPARepository;
    }

    @Override
    public FilaEspera buscar(UUID idFilaEspera) {
        return filaEsperaJPARepository.findById(idFilaEspera)
                .map(this::toFilaEspera)
                .orElse(null);
    }

    private FilaEspera toFilaEspera(FilaEsperaEntity entity) {
        return FilaEspera.create(
                entity.getId(),
                entity.getPacienteNome(),
                entity.getPacienteTelefone(),
                entity.getPacienteEmail(),
                entity.getEspecialidade(),
                entity.getEndereco(),
                entity.getLocalAtendimento(),
                entity.getUnidadeId()
        );
    }
}
