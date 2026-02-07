package br.com.sus.ms_processamento.application.gateway;

import br.com.sus.ms_processamento.domain.model.FilaEspera;

import java.util.UUID;

public interface IFilaEsperaGateway {

    FilaEspera buscar(UUID idFilaEspera);
}
