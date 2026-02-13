package br.com.sus.ms_processamento.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Paciente {

    private String cpf;

    private String nome;

    private String telefone;

    private String email;

    public static Paciente create(String cpf, String nome, String telefone, String email) {
        Paciente paciente = new Paciente();

        paciente.setCpf(cpf);
        paciente.setNome(nome);
        paciente.setTelefone(telefone);
        paciente.setEmail(email);

        return paciente;
    }
}
