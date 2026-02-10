package br.com.sus.ms_processamento.infrastructure.config;

import br.com.sus.ms_processamento.application.gateway.IAgendamentoGateway;
import br.com.sus.ms_processamento.application.usecase.agendamento.AgendamentoUseCase;
import br.com.sus.ms_processamento.application.usecase.agendamento.validation.*;
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
    public AgendamentoConfirmadoHandler agendamentoConfirmadoHandler(IAgendamentoGateway agendamentoGateway) {
        return new AgendamentoConfirmadoHandler(agendamentoGateway);
    }



    @Bean
    public AgendamentoValidationChain agendamentoValidationChain(List<IAgendamentoValidation> agendamentoValidations) {
        return new AgendamentoValidationChain(agendamentoValidations);
    }


    @Bean
    public AgendamentoUseCase confirmarAgendamentoUseCase(
            IAgendamentoGateway agendamentoGateway,
            AgendamentoValidationChain agendamentoValidationChain
    ){
        return AgendamentoUseCase.create(agendamentoGateway, agendamentoValidationChain);
    }
}
