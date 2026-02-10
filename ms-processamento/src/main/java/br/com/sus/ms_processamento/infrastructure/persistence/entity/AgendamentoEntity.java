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

    private String pacienteEmail;

    private LocalDateTime dataHora;

    private String medico;

    private String especialidade;

    private String endereco;

    private String localAtendimento;

    private String unidadeId;

    @Enumerated(EnumType.STRING)
    private StatusAgendamentoEnum status;

    private LocalDateTime dataLimiteConsulta;

    private String tokenUUID;

    @Override
    public String toString() {
        return "AgendamentoEntity{" +
                "id=" + id +
                ", idExterno='" + idExterno + '\'' +
                ", pacienteNome='" + pacienteNome + '\'' +
                ", pacienteTelefone='" + pacienteTelefone + '\'' +
                ", pacienteEmail='" + pacienteEmail + '\'' +
                ", dataHora=" + dataHora +
                ", medico='" + medico + '\'' +
                ", especialidade='" + especialidade + '\'' +
                ", endereco='" + endereco + '\'' +
                ", localAtendimento='" + localAtendimento + '\'' +
                ", unidadeId='" + unidadeId + '\'' +
                ", status=" + status +
                ", dataLimiteConsulta=" + dataLimiteConsulta +
                ", tokenUUID='" + tokenUUID + '\'' +
                '}';
    }

}
