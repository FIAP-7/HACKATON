package br.com.sus.ms_processamento.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class FilaEspera {

    private UUID id;

    private String pacienteNome;

    private String pacienteTelefone;

    private String especialidade;

    private String unidadeSaude;

    private LocalDateTime dataSolicitacao;

    public static FilaEspera create(UUID id, String pacienteNome, String pacienteTelefone, String especialidade, String unidadeSaude) {
        FilaEspera filaEspera = new FilaEspera();

        filaEspera.setId(id);
        filaEspera.setPacienteNome(pacienteNome);
        filaEspera.setPacienteTelefone(pacienteTelefone);
        filaEspera.setEspecialidade(especialidade);
        filaEspera.setUnidadeSaude(unidadeSaude);
        filaEspera.setDataSolicitacao(LocalDateTime.now());

        return filaEspera;
    }


    public static FilaEspera create(String pacienteNome, String pacienteTelefone, String especialidade, String unidadeSaude) {
        FilaEspera filaEspera = new FilaEspera();

        filaEspera.setPacienteNome(pacienteNome);
        filaEspera.setPacienteTelefone(pacienteTelefone);
        filaEspera.setEspecialidade(especialidade);
        filaEspera.setUnidadeSaude(unidadeSaude);
        filaEspera.setDataSolicitacao(LocalDateTime.now());

        return filaEspera;
    }

}
