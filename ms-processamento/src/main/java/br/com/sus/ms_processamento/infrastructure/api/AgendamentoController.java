package br.com.sus.ms_processamento.infrastructure.api;

import br.com.sus.ms_processamento.application.usecase.agendamento.ConfirmarAgendamentoUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgendamentoController {

    private final ConfirmarAgendamentoUseCase confirmarAgendamentoUseCase;

    public AgendamentoController(ConfirmarAgendamentoUseCase confirmarAgendamentoUseCase) {
        this.confirmarAgendamentoUseCase = confirmarAgendamentoUseCase;
    }

    @PostMapping
    public String agendamento(@RequestBody AgendamentoRequest agendamentoRequest) {

        System.out.println("agendamentoRequest = " + agendamentoRequest);

        confirmarAgendamentoUseCase.execute(agendamentoRequest.toInput());

        return "Agendamento salvo com sucesso!";
    }

}
