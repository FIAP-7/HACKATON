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
                public record Paciente(String nome, String telefone, String email) {
                        @JsonCreator
                        public Paciente(@JsonProperty("nome") String nome,
                                        @JsonProperty("telefone") String telefone,
                                        @JsonProperty("email") String email) {
                                this.nome = nome; this.telefone = telefone; this.email = email;
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

        /**
         * Factory method para converter AntecipacaoConsultaEvent em AntecipacaoNotificacaoRecord
         */
        public static AntecipacaoNotificacaoRecord toRecord(AntecipacaoConsultaEvent event) {
                
                return new AntecipacaoNotificacaoRecord(
                        event.consultaCancelada().consulta().especialidade(),
                        event.consultaCancelada().consulta().dataHora() != null ? event.consultaCancelada().consulta().dataHora().toString() : null,
                        event.consultaCancelada().consulta().endereco(),
                        event.consultaCancelada().consulta().localAtendimento(),
                        event.consultaAntecipada().paciente().nome(),
                        event.consultaAntecipada().paciente().email(),
                        event.consultaAntecipada().consulta().especialidade(),
                        event.consultaAntecipada().consulta().dataHora() != null ? event.consultaAntecipada().consulta().dataHora().toString() : null,
                        event.consultaAntecipada().consulta().endereco(),
                        event.consultaAntecipada().consulta().localAtendimento(),
                        event.tokenUUID()
                );
        }
}

