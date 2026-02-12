package br.com.sus.ms_processamento.infrastructure.api.event;

import br.com.sus.ms_processamento.domain.model.Paciente;

import java.time.LocalDateTime;

public record ConfirmacaoAntecipacaoEvent(
        PacienteAntecipacao paciente,
        LocalDateTime novaDataHora,
        String especialidade,
        String medico,
        String localAtendimento,
        String endereco
) {
    public record PacienteAntecipacao(String nome, String email) {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private PacienteAntecipacao paciente;
        private LocalDateTime novaDataHora;
        private String especialidade;
        private String medico;
        private String localAtendimento;
        private String endereco;

        public Builder paciente(Paciente paciente) {
            try {
                this.paciente = new PacienteAntecipacao(paciente.getNome(), paciente.getEmail()) ;
            } catch (Exception e) {
                throw new RuntimeException("Erro ao construir paciente", e);
            }
            return this;
        }

        public Builder novaDataHora(LocalDateTime novaDataHora) {
            this.novaDataHora = novaDataHora;
            return this;
        }

        public Builder especialidade(String especialidade) {
            this.especialidade = especialidade;
            return this;
        }

        public Builder medico(String medico) {
            this.medico = medico;
            return this;
        }

        public Builder localAtendimento(String localAtendimento) {
            this.localAtendimento = localAtendimento;
            return this;
        }

        public Builder endereco(String endereco) {
            this.endereco = endereco;
            return this;
        }

        public ConfirmacaoAntecipacaoEvent build() {
            return new ConfirmacaoAntecipacaoEvent(
                    paciente,
                    novaDataHora,
                    especialidade,
                    medico,
                    localAtendimento,
                    endereco
            );
        }
    }
}
