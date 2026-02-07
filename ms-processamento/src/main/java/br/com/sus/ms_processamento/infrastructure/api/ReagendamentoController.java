package br.com.sus.ms_processamento.infrastructure.api;

import br.com.sus.ms_processamento.application.usecase.reagendamento.ReagendamentoUseCase;
import br.com.sus.ms_processamento.infrastructure.gateway.AgendamentoGateway;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReagendamentoController {

    private final ReagendamentoUseCase reagendamentoUseCase;

    public ReagendamentoController(ReagendamentoUseCase reagendamentoUseCase) {
        this.reagendamentoUseCase = reagendamentoUseCase;
    }

    @PostMapping(value = "criar-reagendamento")
    public String criarReagendamento(ReagendamentoRequest reagendamentoRequest) {
        reagendamentoUseCase.execute(reagendamentoRequest.toCriarReagendamentoInput());

        return "Reagendamento criado com sucesso";
    }
}
