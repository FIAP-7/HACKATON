package br.com.sus.ingestao.entrypoint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PacienteDto(
        @NotBlank(message = "Nome é obrigatório") String nome,
        @NotBlank(message = "Telefone é obrigatório")
        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Formato de telefone inválido") String telefone
) {}
