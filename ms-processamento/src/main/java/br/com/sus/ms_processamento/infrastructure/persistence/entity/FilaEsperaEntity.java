package br.com.sus.ms_processamento.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name="fila_espera")
public class FilaEsperaEntity {

    @Id
    @UuidGenerator
    @GeneratedValue
    private UUID id;

    private String pacienteNome;

    private String pacienteTelefone;

    private String especialidade;

    private String unidadeSaude;

    private LocalDateTime dataSolicitacao;
}
