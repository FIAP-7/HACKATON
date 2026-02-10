package br.com.sus.ms_processamento.infrastructure.api.event;

import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
                public record Paciente(String nome, String cpf, String telefone, String email) {}
                public record Consulta(LocalDateTime dataHora, String medico, String especialidade, String endereco, String localAtendimento, String unidadeId) {}
        }

        /**
         * Factory method para criar AntecipacaoConsultaEvent a partir de duas AgendamentoEntity
         * @param agendamentoCancelado agendamento que foi cancelado
         * @param agendamentoAntecipado agendamento que será antecipado
         * @param tokenUUID token para a antecipação
         */
        public static AntecipacaoConsultaEvent from(AgendamentoEntity agendamentoCancelado, 
                                                     AgendamentoEntity agendamentoAntecipado, 
                                                     String tokenUUID) {
                return new AntecipacaoConsultaEvent(
                        getDadosConsulta(agendamentoCancelado),
                        getDadosConsulta(agendamentoAntecipado),
                        tokenUUID
                );
        }

        private static DadosConsulta getDadosConsulta(AgendamentoEntity agendamentoEntity) {
                return new DadosConsulta(
                        new DadosConsulta.Paciente(
                                agendamentoEntity.getPaciente().getNome(),
                                agendamentoEntity.getPaciente().getCpf(),
                                agendamentoEntity.getPaciente().getTelefone(),
                                agendamentoEntity.getPaciente().getEmail()
                        ),
                        new DadosConsulta.Consulta(
                                agendamentoEntity.getDataHora(),
                                agendamentoEntity.getMedico(),
                                agendamentoEntity.getEspecialidade(),
                                agendamentoEntity.getEndereco(),
                                agendamentoEntity.getLocalAtendimento(),
                                agendamentoEntity.getUnidadeId()
                        )
                );
        }
}

