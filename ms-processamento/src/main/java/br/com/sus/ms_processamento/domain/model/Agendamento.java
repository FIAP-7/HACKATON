package br.com.sus.ms_processamento.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class Agendamento {

    private UUID id;

    private String idExterno;

    private Paciente paciente;

    private LocalDateTime dataHora;

    private String medico;

    private String especialidade;

    private String endereco;

    private String localAtendimento;

    private String unidadeId;

    private StatusAgendamentoEnum status;

    private LocalDateTime dataLimiteConsulta;

    public static Agendamento create(UUID id, String idExterno, Paciente paciente, LocalDateTime dataHora, String medico, String especialidade, String endereco, String localAtendimento, String unidadeId, StatusAgendamentoEnum status, LocalDateTime dataLimiteConsulta) {

        Agendamento agendamento = new Agendamento();

        agendamento.setId(id);
        agendamento.setIdExterno(idExterno);
        agendamento.setPaciente(paciente);
        agendamento.setDataHora(dataHora);
        agendamento.setMedico(medico);
        agendamento.setEspecialidade(especialidade);
        agendamento.setEndereco(endereco);
        agendamento.setLocalAtendimento(localAtendimento);
        agendamento.setUnidadeId(unidadeId);
        agendamento.setStatus(status);
        agendamento.setDataLimiteConsulta(dataLimiteConsulta);

        return agendamento;
    }

    public static Agendamento create(String idExterno, Paciente paciente, LocalDateTime dataHora, String medico, String especialidade, String endereco, String localAtendimento, String unidadeId, StatusAgendamentoEnum status, LocalDateTime dataLimiteConsulta) {

        Agendamento agendamento = new Agendamento();

        agendamento.setIdExterno(idExterno);
        agendamento.setPaciente(paciente);
        agendamento.setDataHora(dataHora);
        agendamento.setMedico(medico);
        agendamento.setEspecialidade(especialidade);
        agendamento.setEndereco(endereco);
        agendamento.setLocalAtendimento(localAtendimento);
        agendamento.setUnidadeId(unidadeId);
        agendamento.setStatus(status);
        agendamento.setDataLimiteConsulta(dataLimiteConsulta);

        return agendamento;
    }

    public static Agendamento create(String idExterno, Paciente paciente, LocalDateTime dataHora, String medico, String especialidade, String endereco, String localAtendimento, String unidadeId, LocalDateTime dataLimiteConsulta) {

        Agendamento agendamento = new Agendamento();

        agendamento.setIdExterno(idExterno);
        agendamento.setPaciente(paciente);
        agendamento.setDataHora(dataHora);
        agendamento.setMedico(medico);
        agendamento.setEspecialidade(especialidade);
        agendamento.setEndereco(endereco);
        agendamento.setLocalAtendimento(localAtendimento);
        agendamento.setUnidadeId(unidadeId);
        agendamento.setStatus(StatusAgendamentoEnum.PENDENTE);
        agendamento.setDataLimiteConsulta(dataLimiteConsulta);

        return agendamento;
    }


}
