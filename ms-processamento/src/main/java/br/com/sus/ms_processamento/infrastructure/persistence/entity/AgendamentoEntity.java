package br.com.sus.ms_processamento.infrastructure.persistence.entity;

import br.com.sus.ms_processamento.domain.model.Agendamento;
import br.com.sus.ms_processamento.domain.model.StatusAgendamentoEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name="agendamento")
public class AgendamentoEntity {

    @Id
    @UuidGenerator
    @GeneratedValue
    private UUID id;

    private String idExterno;

    private String pacienteNome;

    private String pacienteTelefone;

    private LocalDateTime dataHoraConsulta;

    private String medicoNome;

    private String especialidade;

    private String unidadeSaude;

    @Enumerated(EnumType.STRING)
    private StatusAgendamentoEnum status;

    private LocalDateTime dataLimiteConsulta;

    public static AgendamentoEntity build(Agendamento agendamento) {
        return AgendamentoEntity.builder()
                .id(agendamento.getId())
                .idExterno(agendamento.getIdExterno())
                .pacienteNome(agendamento.getPacienteNome())
                .pacienteTelefone(agendamento.getPacienteTelefone())
                .dataHoraConsulta(agendamento.getDataHoraConsulta())
                .medicoNome(agendamento.getMedicoNome())
                .especialidade(agendamento.getEspecialidade())
                .unidadeSaude(agendamento.getUnidadeSaude())
                .status(agendamento.getStatus())
                .dataLimiteConsulta(agendamento.getDataLimiteConsulta())
                .build();
    }

}
