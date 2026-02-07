package br.com.sus.ms_processamento.application.usecase.reagendamento;

import br.com.sus.ms_processamento.application.gateway.IAgendamentoGateway;
import br.com.sus.ms_processamento.application.gateway.IFilaEsperaGateway;
import br.com.sus.ms_processamento.application.gateway.IReagendamentoGateway;
import br.com.sus.ms_processamento.application.usecase.reagendamento.dto.ReagendamentoInput;
import br.com.sus.ms_processamento.application.usecase.reagendamento.presenters.ReagendamentoPresenter;
import br.com.sus.ms_processamento.application.usecase.reagendamento.validation.ReagendamentoValidationChain;
import br.com.sus.ms_processamento.domain.model.Reagendamento;
import br.com.sus.ms_processamento.domain.model.StatusReagendamentoEnum;
import br.com.sus.ms_processamento.infrastructure.gateway.AgendamentoGateway;

public class ReagendamentoUseCase {

    private final ReagendamentoValidationChain agendamentoValidationChain;
    private final IAgendamentoGateway agendamentoGateway;
    private final IReagendamentoGateway reagendamentoGateway;
    private final IFilaEsperaGateway filaEsperaGateway;

    private ReagendamentoUseCase(ReagendamentoValidationChain agendamentoValidationChain,
                                 IAgendamentoGateway agendamentoGateway,
                                 IReagendamentoGateway reagendamentoGateway,
                                 IFilaEsperaGateway filaEsperaGateway
    ) {
        this.agendamentoValidationChain = agendamentoValidationChain;
        this.agendamentoGateway = agendamentoGateway;
        this.reagendamentoGateway = reagendamentoGateway;
        this.filaEsperaGateway = filaEsperaGateway;
    }

    public static ReagendamentoUseCase create(ReagendamentoValidationChain agendamentoValidationChain,
                                              IAgendamentoGateway agendamentoGateway,
                                              IReagendamentoGateway reagendamentoGateway,
                                              IFilaEsperaGateway filaEsperaGateway
    ) {
        return new ReagendamentoUseCase(agendamentoValidationChain, agendamentoGateway, reagendamentoGateway, filaEsperaGateway);
    }

    public void execute(ReagendamentoInput reagendamentoInput){
        Reagendamento reagendamento = initReagendamento(reagendamentoInput);

        agendamentoValidationChain.validate(reagendamento);
    }

    private Reagendamento initReagendamento(ReagendamentoInput reagendamentoInput){
        Reagendamento reagendamento = ReagendamentoPresenter.toReagendamento(reagendamentoInput);

        if(reagendamento.getAgendamento() == null && reagendamento.getIdAgendamento() != null){
            reagendamento.setAgendamento(agendamentoGateway.buscarAgendamento(reagendamento.getIdAgendamento()));
        }

        if(reagendamento.getFilaEspera() != null && reagendamento.getIdFilaEspera() != null){
            reagendamento.setFilaEspera(filaEsperaGateway.buscar(reagendamento.getIdFilaEspera()));
        }

        if(reagendamento.getId() != null && (reagendamento.getAgendamento() == null || reagendamento.getFilaEspera() == null)){
            Reagendamento reagendamentoDatabase = reagendamentoGateway.buscarReagendamentoPorId(reagendamento.getId());

            if (reagendamentoDatabase.getAgendamento() != null) {
                reagendamento.setAgendamento(reagendamentoDatabase.getAgendamento());
            }

            if (reagendamentoDatabase.getFilaEspera() != null) {
                reagendamento.setFilaEspera(reagendamentoDatabase.getFilaEspera());
            }
        }

        if(reagendamento.getId() == null && reagendamento.getAgendamento() != null && reagendamento.getFilaEspera() != null){
            Reagendamento reagendamentoDatabase = reagendamentoGateway.buscarReagendamentoPorAgendamentoEFila(reagendamento.getIdAgendamento(), reagendamento.getIdFilaEspera());

            if(reagendamentoDatabase != null){
                reagendamento = reagendamentoDatabase;
            }
        }

        if (reagendamento.getId() == null) {
            if(!reagendamentoGateway.possuiReagendamentoPendenteResposta(reagendamento.getIdAgendamento())){
                reagendamento.setStatus(StatusReagendamentoEnum.PENDENTE);
            }
        }


        return reagendamento;
    }

}
