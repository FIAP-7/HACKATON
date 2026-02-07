package br.com.sus.ms_processamento.domain.model;

import br.com.sus.ms_processamento.application.exceptions.IdAgendamentoNaoInformadoException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class Reagendamento {

    private UUID id;

    private UUID idAgendamento;

    private UUID idFilaEspera;

    private Agendamento agendamento;

    private FilaEspera filaEspera;

    private StatusReagendamentoEnum status;

    public static Reagendamento create(UUID id,
                                       StatusReagendamentoEnum status,
                                       UUID idAgendamento,
                                       UUID idFilaEspera) {

        Reagendamento reagendamento = create(id, status, idAgendamento);
        reagendamento.setIdFilaEspera(idFilaEspera);

        return reagendamento;
    }

    public static Reagendamento create(UUID id,
                                       StatusReagendamentoEnum status,
                                       UUID idAgendamento) {

        Reagendamento reagendamento = new Reagendamento();

        reagendamento.setId(id);
        reagendamento.setStatus(status);
        reagendamento.setIdAgendamento(idAgendamento);

        return reagendamento;
    }

    public static Reagendamento create(StatusReagendamentoEnum status,
                                       UUID idAgendamento) {

        Reagendamento reagendamento = new Reagendamento();

        reagendamento.setStatus(status);
        reagendamento.setIdAgendamento(idAgendamento);

        return reagendamento;
    }

    public static Reagendamento create(UUID id,
                                       FilaEspera filaEspera,
                                       Agendamento agendamento){

        Reagendamento reagendamento = new Reagendamento();

        reagendamento.setId(id);

        reagendamento.setFilaEspera(filaEspera);
        reagendamento.setIdFilaEspera(filaEspera.getId());

        reagendamento.setAgendamento(agendamento);
        reagendamento.setIdAgendamento(agendamento.getId());

        return reagendamento;
    }

    public void setIdAgendamento(UUID idAgendamento) {
        if(this.idAgendamento == null){
            throw new IdAgendamentoNaoInformadoException();
        }

        this.idAgendamento = idAgendamento;
    }

    public UUID getIdFilaEspera() {
        if(this.idFilaEspera == null && this.filaEspera != null){
            return this.filaEspera.getId();
        }

        return this.idFilaEspera;
    }

    public UUID getIdAgendamento() {
        if(this.idAgendamento == null && this.agendamento != null){
            return this.agendamento.getId();
        }

        return this.idAgendamento;
    }
}
