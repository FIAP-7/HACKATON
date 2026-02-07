package br.com.sus.ms_processamento.infrastructure.persistence.entity;

import br.com.sus.ms_processamento.domain.model.StatusReagendamentoEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@EqualsAndHashCode
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "reagendamento")
public class ReagendamentoEntity {

    @Id
    @UuidGenerator
    private UUID id;

    @Enumerated(EnumType.STRING)
    private StatusReagendamentoEnum status;

    @ManyToOne(fetch = FetchType.LAZY)
    private AgendamentoEntity agendamento;

    @ManyToOne(fetch = FetchType.LAZY)
    private FilaEsperaEntity filaEspera;

}
