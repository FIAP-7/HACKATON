package br.com.sus.ingestao.entrypoint.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ConsultaDto(
        @NotNull(message = "Data/hora é obrigatória")
        @Future(message = "A data deve ser futura")
        LocalDateTime dataHora,
        @NotBlank(message = "Médico é obrigatório") String medico,
        @NotBlank(message = "Especialidade é obrigatória") String especialidade,
        @NotBlank(message = "Unidade é obrigatória") String unidadeId
) {}
