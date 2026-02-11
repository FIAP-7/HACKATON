package br.com.sus.ms_notificacao.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AntecipacaoConsultaEvent(
        DadosConsulta consultaCancelada,
        DadosConsulta consultaAntecipada,
        String tokenUUID
)  {
        public record DadosConsulta(
                Paciente paciente,
                Consulta consulta
        ) {
                @JsonIgnoreProperties(ignoreUnknown = true)
                public record Paciente(String nome, String cpf, String telefone, String email) {
                        @JsonCreator
                        public Paciente(@JsonProperty("nome") String nome,
                                        @JsonProperty("cpf") String cpf,
                                        @JsonProperty("telefone") String telefone,
                                        @JsonProperty("email") String email) {
                                this.nome = nome; this.cpf = cpf; this.telefone = telefone; this.email = email;
                        }
                }

                public record Consulta(LocalDateTime dataHora, String medico, String especialidade, String endereco, String localAtendimento, String unidadeId) {
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
                public DadosConsulta(@JsonProperty("paciente") Paciente paciente,
                                     @JsonProperty("consulta") Consulta consulta) {
                     this.paciente = paciente; this.consulta = consulta;
                }
        }

        @JsonCreator
        public AntecipacaoConsultaEvent(@JsonProperty("consultaCancelada") DadosConsulta consultaCancelada,
                                        @JsonProperty("consultaAntecipada") DadosConsulta consultaAntecipada,
                                        @JsonProperty("tokenUUID") String tokenUUID) {
                this.consultaCancelada = consultaCancelada;
                this.consultaAntecipada = consultaAntecipada;
                this.tokenUUID = tokenUUID;
        }

}

