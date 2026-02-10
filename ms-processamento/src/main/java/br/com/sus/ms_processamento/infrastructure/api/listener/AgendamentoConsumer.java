package br.com.sus.ms_processamento.infrastructure.api.listener;

import br.com.sus.ms_processamento.infrastructure.api.event.AgendamentoEvent;
import br.com.sus.ms_processamento.infrastructure.api.event.ConfirmacaoConsultaEvent;
import br.com.sus.ms_processamento.infrastructure.api.producer.ConfirmacaoConsultaProducer;
import br.com.sus.ms_processamento.infrastructure.config.RabbitMqConfig;
import br.com.sus.ms_processamento.application.usecase.agendamento.ConfirmarAgendamentoUseCase;
import br.com.sus.ms_processamento.infrastructure.api.AgendamentoRequest;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.repository.AgendamentoJPARepository;
import br.com.sus.ms_processamento.domain.model.StatusAgendamentoEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AgendamentoConsumer {

    private static final Logger log = LoggerFactory.getLogger(AgendamentoConsumer.class);

        private final ConfirmarAgendamentoUseCase confirmarAgendamentoUseCase;
        private final AgendamentoJPARepository agendamentoRepository;
        private final ConfirmacaoConsultaProducer confirmacaoProducer;

        public AgendamentoConsumer(ConfirmarAgendamentoUseCase confirmarAgendamentoUseCase,
                                                           AgendamentoJPARepository agendamentoRepository,
                                                           ConfirmacaoConsultaProducer confirmacaoProducer) {
                this.confirmarAgendamentoUseCase = confirmarAgendamentoUseCase;
                this.agendamentoRepository = agendamentoRepository;
                this.confirmacaoProducer = confirmacaoProducer;
        }

    @RabbitListener(queues = RabbitMqConfig.QUEUE_AGENDAMENTO)
    public void processarAgendamento(AgendamentoEvent event) {
        try {
            log.info("[RabbitMQ] Agendamento recebido na fila. idExterno={}", event.toString());
            String tokenUUID = UUID.randomUUID().toString();
            // 1. Salvar agendamento no PostgreSQL
            AgendamentoEntity agendamentoEntity = AgendamentoEntity.builder()
                    .idExterno(event.idExterno())
                    .pacienteNome(event.paciente().nome())
                    .pacienteTelefone(event.paciente().telefone())
                    .pacienteEmail(event.paciente().email())
                    .dataHora(event.consulta().dataHora())
                    .medico(event.consulta().medico())
                    .especialidade(event.consulta().especialidade())
                    .endereco(event.consulta().endereco())
                    .localAtendimento(event.consulta().localAtendimento())
                    .unidadeId(event.consulta().unidadeId())
                    .status(StatusAgendamentoEnum.PENDENTE)
                    .dataLimiteConsulta(event.consulta().dataHora())
                    .tokenUUID(tokenUUID)
                    .build();


            AgendamentoEntity agendamentoSalvo = agendamentoRepository.save(agendamentoEntity);
            log.info("[PostgreSQL] Agendamento salvo com sucesso. dado={}",  agendamentoSalvo.toString());


            // 3. Criar evento de confirmação com o token
            ConfirmacaoConsultaEvent confirmacaoEvent = ConfirmacaoConsultaEvent.from(event, tokenUUID);

            // 4. Enviar evento para a fila de confirmação
            confirmacaoProducer.enviarConfirmacao(confirmacaoEvent);
            log.info("[RabbitMQ] Mensagem de confirmação enviada. idExterno={}, tokenUUID={}", 
                    event.idExterno(), tokenUUID);

            // 5. Executar use case para confirmar agendamento (fluxo existente)
            AgendamentoRequest request = new AgendamentoRequest(
                    agendamentoSalvo.getId(),
                    event.idExterno(),
                    event.paciente().nome(),
                    event.paciente().telefone(),
                    event.paciente().email(),
                    event.consulta().dataHora(),
                    event.consulta().medico(),
                    event.consulta().especialidade(),
                    event.consulta().endereco(),
                    event.consulta().localAtendimento(),
                    event.consulta().unidadeId(),
                    StatusAgendamentoEnum.PENDENTE,
                    event.consulta().dataHora()
            );

            log.info("[RabbitMQ] Processando agendamento. idExterno={}, paciente={}, dataHora={}",
                    event.idExterno(),
                    event.paciente().nome(),
                    event.consulta().dataHora());

            confirmarAgendamentoUseCase.execute(request.toInput());

            log.info("[RabbitMQ] Agendamento processado com sucesso. idExterno={}, tokenUUID={}", 
                    event.idExterno(), tokenUUID);

        } catch (Exception e) {
            log.error("[RabbitMQ] Erro ao processar agendamento. idExterno={}", event.idExterno(), e);
            throw new RuntimeException("Erro ao processar agendamento", e);
        }
    }
}
