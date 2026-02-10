package br.com.sus.ms_notificacao.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record ConfirmacaoConsultaEvent(
        String tokenUUID,
        String idExterno,
        Paciente paciente,
        Consulta consulta,
        LocalDateTime dataIngestao
) {
    public static record Paciente(String nome, String cpf, String telefone, String email) {
        @JsonCreator
        public Paciente(@JsonProperty("nome") String nome,
                        @JsonProperty("cpf") String cpf,
                        @JsonProperty("telefone") String telefone,
                        @JsonProperty("email") String email) {
            this.nome = nome; this.cpf = cpf; this.telefone = telefone; this.email = email;
        }
    }

    public static record Consulta(LocalDateTime dataHora,
                                   String medico,
                                   String especialidade,
                                   String endereco,
                                   String localAtendimento,
                                   String unidadeId) {
        @JsonCreator
        public Consulta(@JsonProperty("dataHora") LocalDateTime dataHora,
                        @JsonProperty("medico") String medico,
                        @JsonProperty("especialidade") String especialidade,
                        @JsonProperty("endereco") String endereco,
                        @JsonProperty("localAtendimento") String localAtendimento,
                        @JsonProperty("unidadeId") String unidadeId) {
            this.dataHora = dataHora; this.medico = medico; this.especialidade = especialidade;
            this.endereco = endereco; this.localAtendimento = localAtendimento; this.unidadeId = unidadeId;
        }
    }

    @JsonCreator
    public ConfirmacaoConsultaEvent(@JsonProperty("tokenUUID") String tokenUUID,
                                    @JsonProperty("idExterno") String idExterno,
                                    @JsonProperty("paciente") Paciente paciente,
                                    @JsonProperty("consulta") Consulta consulta,
                                    @JsonProperty("dataIngestao") LocalDateTime dataIngestao) {
        this.tokenUUID = tokenUUID; this.idExterno = idExterno; this.paciente = paciente; this.consulta = consulta; this.dataIngestao = dataIngestao;
    }
}
