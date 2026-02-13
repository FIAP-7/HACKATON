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

    private final PacienteJPARepository pacienteRepository;
    private final AgendamentoUseCase agendamentoUseCase;
    private final AgendamentoJPARepository agendamentoJPARepository;

    public AgendamentoConsumer(PacienteJPARepository pacienteRepository, AgendamentoJPARepository agendamentoJPARepository ,AgendamentoUseCase agendamentoUseCase) {
                this.pacienteRepository = pacienteRepository;
        this.agendamentoUseCase = agendamentoUseCase;
        this.agendamentoJPARepository = agendamentoJPARepository;
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE_AGENDAMENTO)
    public void processarAgendamento(AgendamentoEvent event) {
        try {
            log.info("[RabbitMQ] Agendamento recebido na fila. idExterno={}", event.toString());
            
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

            AgendamentoEntity agendamentoSalvo = agendamentoJPARepository.save(agendamentoEntity);
            log.info("[PostgreSQL] Agendamento salvo com sucesso. dado={}",  agendamentoSalvo.toString());

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
        } catch (Exception e) {
            log.error("[RabbitMQ] Erro ao processar agendamento. idExterno={}", event.idExterno(), e);
            throw new RuntimeException("Erro ao processar agendamento", e);
        }
    }
}
