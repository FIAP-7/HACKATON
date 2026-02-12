package br.com.sus.ms_processamento.infrastructure.config;

import br.com.sus.ms_processamento.domain.model.StatusAgendamentoEnum;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoPacienteEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.PacienteEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.repository.AgendamentoJPARepository;
import br.com.sus.ms_processamento.infrastructure.persistence.repository.AgendamentoPacienteJPARepository;
import br.com.sus.ms_processamento.infrastructure.persistence.repository.PacienteJPARepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class DataSeederConfig implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeederConfig.class);
    private static final int HORAS_AGENDAMENTO = 10;
    private static final int DIAS_LIMITE_CONSULTA = 3;
    private static final int HORAS_LIMITE_CONSULTA = 23;
    private static final int MINUTOS_LIMITE_CONSULTA = 59;
    private static final int AGENDAMENTOS_PENDENTE_7DIAS = 4;
    private static final int AGENDAMENTOS_PENDENTE_9DIAS = 5;
    private static final int AGENDAMENTOS_POR_PERIODO = 3;
    private static final int QUANTIDADE_AGENDAMENTOS_PSIQUIATRA = 7;
    private static final int CONTADOR_INICIAL_PSIQUIATRA = 100;
    private static final int DIAS_INICIAIS_PSIQUIATRA = 9;
    private static final String[] MEDICOS = {"Dr. Drauzio Varella", "Dra. Sofia Silva", "Dr. Carlos Costa"};
    private static final String[] ESPECIALIDADES = {"Oncologista", "Cardiologista", "Pediatra"};
    private static final String MEDICO_PSIQUIATRA = "Dr. João Psiquiatra";
    private static final String ESPECIALIDADE_PSIQUIATRA = "Psiquiatra";
    private static final String ENDERECO = "Av. Albert Einstein, 627/701 - Morumbi, São Paulo - SP, 05652-900";
    private static final String LOCAL_ATENDIMENTO = "Hospital Albert Einstein";
    private static final String UNIDADE_ID = "UNI-001";
    private static final String EMAIL_PADRAO = "jh93.dev@gmail.com";

    private final AgendamentoJPARepository agendamentoJPARepository;
    private final PacienteJPARepository pacienteJPARepository;
    private final AgendamentoPacienteJPARepository agendamentoPacienteJPARepository;

    public DataSeederConfig(AgendamentoJPARepository agendamentoJPARepository, 
                           PacienteJPARepository pacienteJPARepository,
                           AgendamentoPacienteJPARepository agendamentoPacienteJPARepository) {
        this.agendamentoJPARepository = agendamentoJPARepository;
        this.pacienteJPARepository = pacienteJPARepository;
        this.agendamentoPacienteJPARepository = agendamentoPacienteJPARepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (agendamentoJPARepository.count() == 0) {
            log.info("[DataSeeder] Iniciando população de dados de teste...");
            
            List<AgendamentoEntity> agendamentos = criarAgendamentosPorStatus();
            agendamentos.addAll(criarAgendamentosPsiquiatraPendente());
            
            List<AgendamentoEntity> agendamentosSalvos = agendamentoJPARepository.saveAll(agendamentos);
            log.info("[DataSeeder] {} registros de teste inseridos com sucesso!", agendamentosSalvos.size());
           
            criarAgendamentoPacienteParaAguardandoConfirmacao(agendamentosSalvos);
        } else {
            log.info("[DataSeeder] Tabela agendamento já contém dados. Pulando população de testes.");
        }
    }

    private List<AgendamentoEntity> criarAgendamentosPorStatus() {
        List<AgendamentoEntity> agendamentos = new java.util.ArrayList<>();
        StatusAgendamentoEnum[] statuses = {
                StatusAgendamentoEnum.PENDENTE,
                StatusAgendamentoEnum.AGUARDANDO_CONFIRMACAO,
                StatusAgendamentoEnum.CONFIRMADO_PACIENTE,
                StatusAgendamentoEnum.CANCELADO,
                StatusAgendamentoEnum.ANTECIPAR,
                StatusAgendamentoEnum.REALOCADO
        };

        int contador = 1;
        for (StatusAgendamentoEnum status : statuses) {
            if (status == StatusAgendamentoEnum.PENDENTE) {
                contador = adicionarAgendamentosComDias(agendamentos, status, contador, AGENDAMENTOS_PENDENTE_7DIAS, 7);
                contador = adicionarAgendamentosComDias(agendamentos, status, contador, AGENDAMENTOS_PENDENTE_9DIAS, 9);
            } else {
                contador = adicionarAgendamentosComDias(agendamentos, status, contador, AGENDAMENTOS_POR_PERIODO, 0);
                contador = adicionarAgendamentosComDias(agendamentos, status, contador, AGENDAMENTOS_POR_PERIODO, 5);
                contador = adicionarAgendamentosComDias(agendamentos, status, contador, AGENDAMENTOS_POR_PERIODO, 7);
            }
        }

        return agendamentos;
    }

    private int adicionarAgendamentosComDias(List<AgendamentoEntity> agendamentos, StatusAgendamentoEnum status,
                                             int contador, int quantidade, int dias) {
        for (int i = 0; i < quantidade; i++) {
            agendamentos.add(criarAgendamento(
                    "AGD-" + String.format("%03d", contador++),
                    "Paciente " + contador,
                    gerarCPF(contador),
                    "11" + (99000000 + contador),
                    status,
                    dias
            ));
        }
        return contador;
    }

    private List<AgendamentoEntity> criarAgendamentosPsiquiatraPendente() {
        List<AgendamentoEntity> agendamentos = new java.util.ArrayList<>();
        int contador = CONTADOR_INICIAL_PSIQUIATRA;
        
        for (int i = 0; i < QUANTIDADE_AGENDAMENTOS_PSIQUIATRA; i++) {
            agendamentos.add(criarAgendamentoPsiquiatra(
                    "AGD-PSQ-" + String.format("%03d", i + 1),
                    "Paciente Psiquiatra " + (i + 1),
                    gerarCPF(contador++),
                    "11" + (99000000 + contador),
                    StatusAgendamentoEnum.PENDENTE,
                    DIAS_INICIAIS_PSIQUIATRA + i
            ));
        }
        
        return agendamentos;
    }

    private void criarAgendamentoPacienteParaAguardandoConfirmacao(List<AgendamentoEntity> agendamentos) {
        agendamentos.stream()
                .filter(ag -> ag.getStatus() == StatusAgendamentoEnum.AGUARDANDO_CONFIRMACAO)
                .forEach(ag -> {
                    AgendamentoPacienteEntity ap = AgendamentoPacienteEntity.builder()
                            .paciente(ag.getPaciente())
                            .agendamento(ag)
                            .dataRegistro(LocalDateTime.now())
                            .status(StatusAgendamentoEnum.AGUARDANDO_CONFIRMACAO.toString())
                            .token(UUID.randomUUID().toString())
                            .build();
                    agendamentoPacienteJPARepository.save(ap);
                });
    }

    private String gerarCPF(int numero) {
        return String.format("%010d1", numero % 10000000000L);
    }

    private AgendamentoEntity criarAgendamento(String idExterno, String pacienteNome, String cpf, 
                                               String pacienteTelefone, StatusAgendamentoEnum status, int diasAdicionar) {
        PacienteEntity paciente = obterOuCriarPaciente(pacienteNome, cpf, pacienteTelefone);
        int indiceEspecialidade = Math.abs(cpf.hashCode() % ESPECIALIDADES.length);
        
        return construirAgendamento(idExterno, paciente, diasAdicionar, status,
                MEDICOS[indiceEspecialidade], ESPECIALIDADES[indiceEspecialidade]);
    }

    private AgendamentoEntity criarAgendamentoPsiquiatra(String idExterno, String pacienteNome, String cpf, 
                                                        String pacienteTelefone, StatusAgendamentoEnum status, int diasAdicionar) {
        PacienteEntity paciente = obterOuCriarPaciente(pacienteNome, cpf, pacienteTelefone);
        
        return construirAgendamento(idExterno, paciente, diasAdicionar, status,
                MEDICO_PSIQUIATRA, ESPECIALIDADE_PSIQUIATRA);
    }

    private PacienteEntity obterOuCriarPaciente(String nome, String cpf, String telefone) {
        return pacienteJPARepository.findById(cpf)
            .orElseGet(() -> {
                PacienteEntity novoPaciente = PacienteEntity.builder()
                    .nome(nome)
                    .cpf(cpf)
                    .telefone(telefone)
                    .email(obterEmailSeeder())
                    .build();
                return pacienteJPARepository.save(novoPaciente);
            });
    }

    private String obterEmailSeeder() {
        String emailAmbiente = System.getenv("SEEDER_EMAIL");
        return (emailAmbiente != null && !emailAmbiente.isBlank()) ? emailAmbiente : EMAIL_PADRAO;
    }

    private AgendamentoEntity construirAgendamento(String idExterno, PacienteEntity paciente, int diasAdicionar,
                                                   StatusAgendamentoEnum status, String medico, String especialidade) {
        LocalDateTime dataHora = calcularDataHora(diasAdicionar);
        LocalDateTime dataLimite = calcularDataLimite(dataHora);

        return AgendamentoEntity.builder()
            .idExterno(idExterno)
            .paciente(paciente)
            .dataHora(dataHora)
            .medico(medico)
            .especialidade(especialidade)
            .endereco(ENDERECO)
            .localAtendimento(LOCAL_ATENDIMENTO)
            .unidadeId(UNIDADE_ID)
            .status(status)
            .dataLimiteConsulta(dataLimite)
            .build();
    }

    private LocalDateTime calcularDataHora(int diasAdicionar) {
        return LocalDateTime.now()
            .plusDays(diasAdicionar)
            .withHour(HORAS_AGENDAMENTO)
            .withMinute(0)
            .withSecond(0)
            .withNano(0);
    }

    private LocalDateTime calcularDataLimite(LocalDateTime dataHora) {
        return dataHora
            .plusDays(DIAS_LIMITE_CONSULTA)
            .withHour(HORAS_LIMITE_CONSULTA)
            .withMinute(MINUTOS_LIMITE_CONSULTA)
            .withSecond(59)
            .withNano(0);
    }
}
