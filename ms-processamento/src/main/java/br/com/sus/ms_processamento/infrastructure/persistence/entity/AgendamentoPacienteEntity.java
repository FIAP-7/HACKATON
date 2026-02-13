package br.com.sus.ms_processamento.infrastructure.persistence.entity;

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
@Table(name = "agendamento_paciente", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"paciente_id", "agendamento_id"})
})
public class AgendamentoPacienteEntity {

    @Id
    @UuidGenerator
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paciente_id", nullable = false)
    private PacienteEntity paciente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agendamento_id", nullable = false)
    private AgendamentoEntity agendamento;

    @Column(nullable = false)
    private LocalDateTime dataRegistro;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, length = 255)
    private String token;

    @Override
    public String toString() {
        return "AgendamentoPacienteEntity{" +
                "id=" + id +
                ", paciente=" + paciente +
                ", agendamento=" + agendamento +
                ", dataRegistro=" + dataRegistro +
                ", status='" + status + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
