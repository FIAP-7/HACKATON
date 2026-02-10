package br.com.sus.ms_processamento.infrastructure.api.listener;

import br.com.sus.ms_processamento.application.usecase.agendamento.AgendamentoUseCase;
import br.com.sus.ms_processamento.infrastructure.api.AgendamentoRequest;
import br.com.sus.ms_processamento.infrastructure.api.event.AgendamentoEvent;
import br.com.sus.ms_processamento.infrastructure.api.event.ConfirmacaoConsultaEvent;
import br.com.sus.ms_processamento.infrastructure.api.producer.ConfirmacaoConsultaProducer;
import br.com.sus.ms_processamento.infrastructure.config.RabbitMqConfig;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoPacienteEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.PacienteEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.repository.AgendamentoJPARepository;
import br.com.sus.ms_processamento.infrastructure.persistence.repository.AgendamentoPacienteJPARepository;
import br.com.sus.ms_processamento.infrastructure.persistence.repository.PacienteJPARepository;
import br.com.sus.ms_processamento.domain.model.StatusAgendamentoEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class AgendamentoConsumer {

    private static final Logger log = LoggerFactory.getLogger(AgendamentoConsumer.class);

        private final AgendamentoJPARepository agendamentoRepository;
        private final PacienteJPARepository pacienteRepository;
        private final AgendamentoPacienteJPARepository agendamentoPacienteRepository;
        private final ConfirmacaoConsultaProducer confirmacaoProducer;
    private final AgendamentoUseCase agendamentoUseCase;

    public AgendamentoConsumer(AgendamentoJPARepository agendamentoRepository,
                                   PacienteJPARepository pacienteRepository,
                                   AgendamentoPacienteJPARepository agendamentoPacienteRepository,
                                   ConfirmacaoConsultaProducer confirmacaoProducer, AgendamentoUseCase agendamentoUseCase) {
                this.agendamentoRepository = agendamentoRepository;
                this.pacienteRepository = pacienteRepository;
                this.agendamentoPacienteRepository = agendamentoPacienteRepository;
                this.confirmacaoProducer = confirmacaoProducer;
        this.agendamentoUseCase = agendamentoUseCase;
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE_AGENDAMENTO)
    public void processarAgendamento(AgendamentoEvent event) {
        try {
            log.info("[RabbitMQ] Agendamento recebido na fila. idExterno={}", event.toString());
            String tokenUUID = UUID.randomUUID().toString();
            
            PacienteEntity pacienteEntity = pacienteRepository.findById(event.paciente().cpf())
                    .orElseGet(() -> {
                        PacienteEntity novoPaciente = PacienteEntity.builder()
                                .nome(event.paciente().nome())
                                .cpf(event.paciente().cpf())
                                .telefone(event.paciente().telefone())
                                .email(event.paciente().email())
                                .build();
                        return pacienteRepository.save(novoPaciente);
                    });
            
            AgendamentoEntity agendamentoEntity = AgendamentoEntity.builder()
                    .idExterno(event.idExterno())
                    .paciente(pacienteEntity)
                    .dataHora(event.consulta().dataHora())
                    .medico(event.consulta().medico())
                    .especialidade(event.consulta().especialidade())
                    .endereco(event.consulta().endereco())
                    .localAtendimento(event.consulta().localAtendimento())
                    .unidadeId(event.consulta().unidadeId())
                    .status(StatusAgendamentoEnum.PENDENTE)
                    .dataLimiteConsulta(event.consulta().dataHora())
                    .build();

            AgendamentoEntity agendamentoSalvo = agendamentoRepository.save(agendamentoEntity);
            log.info("[PostgreSQL] Agendamento salvo com sucesso. dado={}",  agendamentoSalvo.toString());

            AgendamentoPacienteEntity agendamentoPaciente = AgendamentoPacienteEntity.builder()
                    .paciente(pacienteEntity)
                    .agendamento(agendamentoSalvo)
                    .dataRegistro(LocalDateTime.now())
                    .status(StatusAgendamentoEnum.PENDENTE.toString())
                    .token(tokenUUID)
                    .build();
            agendamentoPacienteRepository.save(agendamentoPaciente);
            log.info("[PostgreSQL] Registro agendamento_paciente salvo com sucesso.");

            ConfirmacaoConsultaEvent confirmacaoEvent = ConfirmacaoConsultaEvent.from(event, tokenUUID);

            confirmacaoProducer.enviarConfirmacao(confirmacaoEvent);
            log.info("[RabbitMQ] Mensagem de confirmação enviada. idExterno={}, tokenUUID={}", 
                    event.idExterno(), tokenUUID);

            AgendamentoRequest request = new AgendamentoRequest(
                    agendamentoSalvo.getId(),
                    event.idExterno(),
                    event.paciente().nome(),
                    event.paciente().cpf(),
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

            agendamentoUseCase.execute(request.toInput());

            log.info("[RabbitMQ] Agendamento processado com sucesso. idExterno={}, tokenUUID={}", 
                    event.idExterno(), tokenUUID);

        } catch (Exception e) {
            log.error("[RabbitMQ] Erro ao processar agendamento. idExterno={}", event.idExterno(), e);
            throw new RuntimeException("Erro ao processar agendamento", e);
        }
    }
}
