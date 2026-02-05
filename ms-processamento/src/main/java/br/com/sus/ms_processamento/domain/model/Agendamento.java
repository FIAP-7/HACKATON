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

    private String pacienteNome;

    private String pacienteTelefone;

    private LocalDateTime dataHoraConsulta;

    private String medicoNome;

    private String especialidade;

    private String unidadeSaude;

    private StatusAgendamentoEnum status;

    private LocalDateTime dataLimiteConsulta;

    public static Agendamento create(UUID id, String idExterno, String pacienteNome, String pacienteTelefone, LocalDateTime dataHoraConsulta, String medicoNome, String especialidade, String unidadeSaude, StatusAgendamentoEnum status, LocalDateTime dataLimiteConsulta) {

        Agendamento agendamento = new Agendamento();

        agendamento.setId(id);
        agendamento.setIdExterno(idExterno);
        agendamento.setPacienteNome(pacienteNome);
        agendamento.setPacienteTelefone(pacienteTelefone);
        agendamento.setDataHoraConsulta(dataHoraConsulta);
        agendamento.setMedicoNome(medicoNome);
        agendamento.setEspecialidade(especialidade);
        agendamento.setUnidadeSaude(unidadeSaude);
        agendamento.setStatus(status);
        agendamento.setDataLimiteConsulta(dataLimiteConsulta);

        return agendamento;
    }

    public static Agendamento create(String idExterno, String pacienteNome, String pacienteTelefone, LocalDateTime dataHoraConsulta, String medicoNome, String especialidade, String unidadeSaude, StatusAgendamentoEnum status, LocalDateTime dataLimiteConsulta) {

        Agendamento agendamento = new Agendamento();

        agendamento.setIdExterno(idExterno);
        agendamento.setPacienteNome(pacienteNome);
        agendamento.setPacienteTelefone(pacienteTelefone);
        agendamento.setDataHoraConsulta(dataHoraConsulta);
        agendamento.setMedicoNome(medicoNome);
        agendamento.setEspecialidade(especialidade);
        agendamento.setUnidadeSaude(unidadeSaude);
        agendamento.setStatus(status);
        agendamento.setDataLimiteConsulta(dataLimiteConsulta);

        return agendamento;
    }

    public static Agendamento create(String idExterno, String pacienteNome, String pacienteTelefone, LocalDateTime dataHoraConsulta, String medicoNome, String especialidade, String unidadeSaude, LocalDateTime dataLimiteConsulta) {

        Agendamento agendamento = new Agendamento();

        agendamento.setIdExterno(idExterno);
        agendamento.setPacienteNome(pacienteNome);
        agendamento.setPacienteTelefone(pacienteTelefone);
        agendamento.setDataHoraConsulta(dataHoraConsulta);
        agendamento.setMedicoNome(medicoNome);
        agendamento.setEspecialidade(especialidade);
        agendamento.setUnidadeSaude(unidadeSaude);
        agendamento.setStatus(StatusAgendamentoEnum.PENDENTE);
        agendamento.setDataLimiteConsulta(dataLimiteConsulta);

        return agendamento;
    }


}
