package br.com.sus.ms_processamento.application.usecase.reagendamento.validation;

import br.com.sus.ms_processamento.application.gateway.IAgendamentoGateway;
import br.com.sus.ms_processamento.application.gateway.IReagendamentoGateway;
import br.com.sus.ms_processamento.domain.model.Agendamento;
import br.com.sus.ms_processamento.domain.model.Reagendamento;
import br.com.sus.ms_processamento.domain.model.StatusAgendamentoEnum;
import br.com.sus.ms_processamento.domain.model.StatusReagendamentoEnum;

public class ReagendamentoConfirmadoHandler implements IReagendamentoValidation{

    private final IReagendamentoGateway reagendamentoGateway;
    private final IAgendamentoGateway agendamentoGateway;

    public ReagendamentoConfirmadoHandler(IReagendamentoGateway reagendamentoGateway, IAgendamentoGateway agendamentoGateway) {
        this.reagendamentoGateway = reagendamentoGateway;
        this.agendamentoGateway = agendamentoGateway;
    }

    @Override
    public void validate(Reagendamento reagendamento) {
        if(StatusReagendamentoEnum.CONFIRMADO.equals(reagendamento.getStatus())){
            Agendamento agendamento = reagendamentoGateway.buscarAgendamentoPorId(reagendamento.getIdAgendamento());

            if(agendamento.getStatus().equals(StatusAgendamentoEnum.REALOCADO)){
                reagendamentoGateway.enviarInformativoJaAlocado(reagendamento);
            }else{
                agendamentoGateway.atualizarStatusAgendamento(agendamento.getId(), StatusAgendamentoEnum.REALOCADO);
                reagendamentoGateway.atualizarStatusReagendamento(reagendamento.getId(), StatusReagendamentoEnum.CONFIRMADO);

                reagendamentoGateway.enviarInformativoConfirmado(reagendamento);
            }
        }
    }
}
