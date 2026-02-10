package br.com.sus.ms_processamento.infrastructure.api;

import br.com.sus.ms_processamento.application.usecase.agendamento.AgendamentoUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgendamentoController {

    private final AgendamentoUseCase agendamentoUseCase;

    public AgendamentoController(AgendamentoUseCase agendamentoUseCase) {
        this.agendamentoUseCase = agendamentoUseCase;
    }

    @PostMapping
    public String agendamento(@RequestBody AgendamentoRequest agendamentoRequest) {

        System.out.println("agendamentoRequest = " + agendamentoRequest);

        agendamentoUseCase.execute(agendamentoRequest.toInput());

        return "Agendamento salvo com sucesso!";
    }

}
