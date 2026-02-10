package br.com.sus.ingestao.entrypoint.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PacienteDto(
        @NotBlank(message = "Nome é obrigatório") String nome,
        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$|^\\d{11}$", message = "Formato de CPF inválido. Use XXX.XXX.XXX-XX ou 11 dígitos.") String cpf,
        @NotBlank(message = "Telefone é obrigatório")
        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Formato de telefone inválido") String telefone,
        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido") String email
) {}
