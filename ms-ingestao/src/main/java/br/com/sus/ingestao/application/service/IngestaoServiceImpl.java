package br.com.sus.ingestao.application.service;

import br.com.sus.ingestao.core.event.AgendamentoEvent;
import br.com.sus.ingestao.core.event.EventoRespostaUsuario;
import br.com.sus.ingestao.core.port.AgendamentoPublisherPort;
import br.com.sus.ingestao.core.port.RespostaPublisherPort;
import br.com.sus.ingestao.core.usecase.IngestaoService;
import br.com.sus.ingestao.core.usecase.model.AgendamentoCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Application layer implementation for the Ingestao use case.
 * Depends on ports and domain events only. Wired by Spring in the application layer.
 */
@Service
public class IngestaoServiceImpl implements IngestaoService {

    private static final Logger log = LoggerFactory.getLogger(IngestaoServiceImpl.class);

    private final AgendamentoPublisherPort agendamentoPublisher;
    private final RespostaPublisherPort respostaPublisher;

    public IngestaoServiceImpl(AgendamentoPublisherPort agendamentoPublisher, RespostaPublisherPort respostaPublisher) {
        this.agendamentoPublisher = agendamentoPublisher;
        this.respostaPublisher = respostaPublisher;
    }

    @Override
    public void processarAgendamento(AgendamentoCommand command) {
        // Log recebimento conforme US-01
        log.info("[Ingestao] Recebido com sucesso - idExterno={}, paciente={}, consultaDataHora={}",
                command.idExterno(), command.paciente().nome(), command.consulta().dataHora());

        // Monta evento com timestamp de ingestão e publica (US-02)
        AgendamentoEvent event = new AgendamentoEvent(
                command.idExterno(),
                new AgendamentoEvent.Paciente(command.paciente().nome(), command.paciente().telefone(), command.paciente().email()),
                new AgendamentoEvent.Consulta(
                        command.consulta().dataHora(),
                        command.consulta().medico(),
                        command.consulta().endereco(),
                        command.consulta().localAtendimento(),
                        command.consulta().especialidade(),
                        command.consulta().unidadeId()
                ),
                LocalDateTime.now()
        );

        agendamentoPublisher.publicar(event);
    }

    @Override
    public void processarAcaoEmail(String token, String acao) {
        EventoRespostaUsuario evt = new EventoRespostaUsuario(token, acao, EventoRespostaUsuario.CanalNotificacao.EMAIL, LocalDateTime.now());
        respostaPublisher.publicar(evt);
        log.info("[AcaoEmail] Resposta publicada - token={}, acao={}", token, acao);
    }

    @Override
    public void processarAntecipacaoEmail(String token, String acao) {
        EventoRespostaUsuario evt = new EventoRespostaUsuario(token, acao, EventoRespostaUsuario.CanalNotificacao.EMAIL, LocalDateTime.now());
        respostaPublisher.publicarAntecipacao(evt);
        log.info("[AcaoEmail] Resposta publicada - token={}, acao={}", token, acao);
    }

}
