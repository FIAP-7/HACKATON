package br.com.sus.ms_processamento.application.exceptions;

public class IdAgendamentoNaoInformadoException extends RuntimeException {
    public IdAgendamentoNaoInformadoException() {
        super("Id do agendamento nao foi informado");
    }
}
