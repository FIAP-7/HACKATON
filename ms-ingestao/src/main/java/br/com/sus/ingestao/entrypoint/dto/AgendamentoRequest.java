package br.com.sus.ingestao.entrypoint.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AgendamentoRequest(
        @NotBlank(message = "idExterno é obrigatório") String idExterno,
        @NotNull(message = "Paciente é obrigatório") @Valid PacienteDto paciente,
        @NotNull(message = "Consulta é obrigatória") @Valid ConsultaDto consulta
) {}
