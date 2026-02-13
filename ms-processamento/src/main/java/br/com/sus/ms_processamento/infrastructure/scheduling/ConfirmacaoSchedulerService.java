package br.com.sus.ms_processamento.infrastructure.scheduling;

import br.com.sus.ms_processamento.domain.model.Agendamento;
import br.com.sus.ms_processamento.domain.model.StatusAgendamentoEnum;
import br.com.sus.ms_processamento.infrastructure.gateway.AgendamentoGateway;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.repository.AgendamentoJPARepository;
import br.com.sus.ms_processamento.infrastructure.persistence.repository.AgendamentoPacienteJPARepository;
import br.com.sus.ms_processamento.infrastructure.presenters.AgendamentoEntityPresenters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ConfirmacaoSchedulerService {

    private final AgendamentoJPARepository agendamentoJPARepository;
    private final AgendamentoGateway agendamentoGateway;

    public ConfirmacaoSchedulerService(AgendamentoJPARepository agendamentoJPARepository, AgendamentoGateway agendamentoGateway) {
        this.agendamentoJPARepository = agendamentoJPARepository;
        this.agendamentoGateway = agendamentoGateway;
    }

   @Scheduled(fixedDelay = 240000)
    public void enviarConfirmacoesPreventivas() {
       enviarConfirmacaoPendentes7Dias();

       enviarAntecipacao();
   }

   private void enviarAntecipacao(){
       List<AgendamentoEntity> agendamentosRealocar = agendamentoJPARepository.findByStatus(StatusAgendamentoEnum.ANTECIPAR);
       
       log.info("Encontrados {} agendamentos com status ANTECIPAR para realocação", agendamentosRealocar.size());
       int processed = 0;
       
       for (AgendamentoEntity agendamento : agendamentosRealocar) {
           Agendamento agendamentoDomain = AgendamentoEntityPresenters.toDomain(agendamento);
           agendamentoGateway.realocarAgendamento(agendamentoDomain);
           processed++;
       }
       
       log.info("Processados {} agendamentos para realocação", processed);
   }

    private void enviarConfirmacaoPendentes7Dias() {
        LocalDate target = LocalDate.now().plusDays(7);
        LocalDateTime start = target.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        List<AgendamentoEntity> candidatos = agendamentoJPARepository.findByStatusAndDataHoraBetween(
                StatusAgendamentoEnum.PENDENTE, start, end);

        log.info("Encontrados {} agendamentos PENDENTE para D+7 ({} -> {})", candidatos.size(), start, end);
        int processed = 0;

        for (AgendamentoEntity agendamento : candidatos) {
            Agendamento agendamentoDomain = AgendamentoEntityPresenters.toDomain(agendamento);
            agendamentoGateway.enviarConfirmacao(agendamentoDomain);
            processed++;
        }
        log.info("Processadas {} notificações de confirmação preventiva", processed);
    }

}