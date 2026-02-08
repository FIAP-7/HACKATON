package br.com.sus.ms_processamento.infrastructure.config;

import br.com.sus.ms_processamento.application.gateway.IAgendamentoGateway;
import br.com.sus.ms_processamento.application.gateway.IFilaEsperaGateway;
import br.com.sus.ms_processamento.application.gateway.IReagendamentoGateway;
import br.com.sus.ms_processamento.application.usecase.reagendamento.ReagendamentoUseCase;
import br.com.sus.ms_processamento.application.usecase.reagendamento.validation.IReagendamentoValidation;
import br.com.sus.ms_processamento.application.usecase.reagendamento.validation.ReagendamentoConfirmadoHandler;
import br.com.sus.ms_processamento.application.usecase.reagendamento.validation.ReagendamentoPendenteHandler;
import br.com.sus.ms_processamento.application.usecase.reagendamento.validation.ReagendamentoValidationChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ReagendamentoUseCaseConfig {

    @Bean
    public ReagendamentoPendenteHandler reagendamentoPendenteHandler(IReagendamentoGateway reagendamentoGateway) {
        return new ReagendamentoPendenteHandler(reagendamentoGateway);
    }

    @Bean
    public ReagendamentoConfirmadoHandler reagendamentoConfirmadoHandler(IReagendamentoGateway reagendamentoGateway, IAgendamentoGateway agendamentoGateway) {
        return new ReagendamentoConfirmadoHandler(reagendamentoGateway, agendamentoGateway);
    }


    @Bean
    public ReagendamentoValidationChain reagendamentoValidationChain(List<IReagendamentoValidation> reagendamentoValidations) {
        return new ReagendamentoValidationChain(reagendamentoValidations);
    }

    @Bean
    public ReagendamentoUseCase reagendamentoUseCase(
            ReagendamentoValidationChain validationChain,
            IAgendamentoGateway agendamentoGateway,
            IReagendamentoGateway reagendamentoGateway,
            IFilaEsperaGateway filaEsperaGateway
    ) {
        return ReagendamentoUseCase.create(
                validationChain,
                agendamentoGateway,
                reagendamentoGateway,
                filaEsperaGateway
        );
    }
}

