package br.com.sus.ms_processamento.infrastructure.config;

import br.com.sus.ms_processamento.application.gateway.IAgendamentoGateway;
import br.com.sus.ms_processamento.application.usecase.agendamento.ConfirmarAgendamentoUseCase;
import br.com.sus.ms_processamento.application.usecase.agendamento.validation.AgendamentoCanceladoHandler;
import br.com.sus.ms_processamento.application.usecase.agendamento.validation.AgendamentoPendenteHandler;
import br.com.sus.ms_processamento.application.usecase.agendamento.validation.AgendamentoValidationChain;
import br.com.sus.ms_processamento.application.usecase.agendamento.validation.IAgendamentoValidation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AgendamentoUseCaseConfig {

    @Bean
    public AgendamentoPendenteHandler agendamentoPendenteHandler(IAgendamentoGateway agendamentoGateway) {
        return new AgendamentoPendenteHandler(agendamentoGateway);
    }

    @Bean
    public AgendamentoCanceladoHandler agendamentoCanceladoHandler(IAgendamentoGateway agendamentoGateway) {
        return new AgendamentoCanceladoHandler(agendamentoGateway);
    }



    @Bean
    public AgendamentoValidationChain agendamentoValidationChain(List<IAgendamentoValidation> agendamentoValidations) {
        return new AgendamentoValidationChain(agendamentoValidations);
    }


    @Bean
    public ConfirmarAgendamentoUseCase confirmarAgendamentoUseCase(
            IAgendamentoGateway agendamentoGateway,
            AgendamentoValidationChain agendamentoValidationChain
    ){
        return ConfirmarAgendamentoUseCase.create(agendamentoGateway, agendamentoValidationChain);
    }
}
