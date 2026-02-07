package br.com.sus.ms_processamento.application.usecase.reagendamento.validation;

import br.com.sus.ms_processamento.application.gateway.IReagendamentoGateway;
import br.com.sus.ms_processamento.domain.model.Reagendamento;
import br.com.sus.ms_processamento.domain.model.StatusReagendamentoEnum;

import java.util.List;

public class ReagendamentoPendenteHandler implements IReagendamentoValidation{

    private final IReagendamentoGateway reagendamentoGateway;

    public ReagendamentoPendenteHandler(IReagendamentoGateway reagendamentoGateway) {
        this.reagendamentoGateway = reagendamentoGateway;
    }

    @Override
    public void validate(Reagendamento reagendamento) {
        if(StatusReagendamentoEnum.PENDENTE.equals(reagendamento.getStatus())){
            List<Reagendamento> reagendamentos = reagendamentoGateway.buscarDisponivelParaReagendamento(reagendamento);

            reagendamentos.forEach(val -> val.setStatus(StatusReagendamentoEnum.PENDENTE_RESPOSTA));

            reagendamentoGateway.salvar(reagendamentos);

            reagendamentoGateway.enviarOpcaoReagendamento(reagendamentos);
        }
    }
}
