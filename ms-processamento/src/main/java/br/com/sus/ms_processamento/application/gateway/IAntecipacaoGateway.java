package br.com.sus.ms_processamento.application.gateway;

public interface IAntecipacaoGateway {

    void confirmarAntecipacao(String token);

    void recusarAntecipacao(String token);
}
