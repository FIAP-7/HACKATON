package br.com.sus.ms_notificacao.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record ConfirmacaoAntecipacaoEvent(
        Paciente paciente,
        LocalDateTime novaDataHora,
        String especialidade,
        String medico,
        String localAtendimento,
        String endereco
) {
    public static record Paciente(Long id, String nome, String cpf, String email) {
        @JsonCreator
        public Paciente(@JsonProperty("id") Long id,
                        @JsonProperty("nome") String nome,
                        @JsonProperty("cpf") String cpf,
                        @JsonProperty("email") String email) {
            this.id = id;
            this.nome = nome;
            this.cpf = cpf;
            this.email = email;
        }
    }

    @JsonCreator
    public ConfirmacaoAntecipacaoEvent(
            @JsonProperty("paciente") Paciente paciente,
            @JsonProperty("novaDataHora") LocalDateTime novaDataHora,
            @JsonProperty("especialidade") String especialidade,
            @JsonProperty("medico") String medico,
            @JsonProperty("localAtendimento") String localAtendimento,
            @JsonProperty("endereco") String endereco) {
        this.paciente = paciente;
        this.novaDataHora = novaDataHora;
        this.especialidade = especialidade;
        this.medico = medico;
        this.localAtendimento = localAtendimento;
        this.endereco = endereco;
    }
}
