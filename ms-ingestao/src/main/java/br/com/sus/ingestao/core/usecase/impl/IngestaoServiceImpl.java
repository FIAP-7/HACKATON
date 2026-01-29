package br.com.sus.ingestao.core.usecase.impl;

import br.com.sus.ingestao.core.event.AgendamentoEvent;
import br.com.sus.ingestao.core.port.AgendamentoPublisherPort;
import br.com.sus.ingestao.core.usecase.IngestaoService;
import br.com.sus.ingestao.entrypoint.dto.AgendamentoRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class IngestaoServiceImpl implements IngestaoService {

    private static final Logger log = LoggerFactory.getLogger(IngestaoServiceImpl.class);

    private final AgendamentoPublisherPort publisher;

    public IngestaoServiceImpl(AgendamentoPublisherPort publisher) {
        this.publisher = publisher;
    }

    @Override
    public void processarAgendamento(AgendamentoRequest request) {
        // Log recebimento conforme US-01
        log.info("[Ingestao] Recebido com sucesso - idExterno={}, paciente={}, consultaDataHora={}",
                request.idExterno(), request.paciente().nome(), request.consulta().dataHora());

        // Monta evento com timestamp de ingestão e publica (US-02)
        AgendamentoEvent event = new AgendamentoEvent(
                request.idExterno(),
                new AgendamentoEvent.Paciente(request.paciente().nome(), request.paciente().telefone()),
                new AgendamentoEvent.Consulta(
                        request.consulta().dataHora(),
                        request.consulta().medico(),
                        request.consulta().especialidade(),
                        request.consulta().unidadeId()
                ),
                LocalDateTime.now()
        );

        publisher.publicar(event);
    }
}
