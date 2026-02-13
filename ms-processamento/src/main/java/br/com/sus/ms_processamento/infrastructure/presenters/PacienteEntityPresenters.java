package br.com.sus.ms_processamento.infrastructure.presenters;

import br.com.sus.ms_processamento.domain.model.Paciente;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.PacienteEntity;

public class PacienteEntityPresenters {

    public static PacienteEntity toEntity(Paciente paciente) {
        return PacienteEntity.builder()
                .cpf(paciente.getCpf())
                .nome(paciente.getNome())
                .telefone(paciente.getTelefone())
                .email(paciente.getEmail())
                .build();
    }

    public static Paciente toDomain(PacienteEntity pacienteEntity) {
        return Paciente.create(
                pacienteEntity.getCpf(),
                pacienteEntity.getNome(),
                pacienteEntity.getTelefone(),
                pacienteEntity.getEmail()
        );
    }
}
