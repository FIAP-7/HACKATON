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
            
            List<AgendamentoEntity> agendamentos = Arrays.asList(
                criarAgendamento("AGD-001", "João Silva", "12345678901", "11999999901", StatusAgendamentoEnum.AGUARDANDO_CONFIRMACAO),
                criarAgendamento("AGD-002", "Maria Santos", "12345678902", "11999999902", StatusAgendamentoEnum.CONFIRMADO_PACIENTE),
                criarAgendamento("AGD-003", "Pedro Costa", "12345678903", "11999999903", StatusAgendamentoEnum.CANCELADO),
                criarAgendamento("AGD-004", "Ana Oliveira", "12345678904", "11999999904", StatusAgendamentoEnum.AGUARDANDO_CONFIRMACAO),
                criarAgendamento("AGD-005", "Lucas Ferreira", "12345678905", "11999999905", StatusAgendamentoEnum.CONFIRMADO_PACIENTE),
                criarAgendamento("AGD-006", "Carla Martins", "12345678906", "11999999906", StatusAgendamentoEnum.AGUARDANDO_CONFIRMACAO),
                criarAgendamento("AGD-007", "Ricardo Souza", "12345678907", "11999999907", StatusAgendamentoEnum.CANCELADO),
                criarAgendamento("AGD-008", "Beatriz Lima", "12345678908", "11999999908", StatusAgendamentoEnum.CONFIRMADO_PACIENTE),
                criarAgendamento("AGD-009", "Gabriel Rocha", "12345678909", "11999999909", StatusAgendamentoEnum.AGUARDANDO_CONFIRMACAO),
                criarAgendamento("AGD-010", "Fernanda Santos", "12345678910", "11999999910", StatusAgendamentoEnum.CANCELADO),
                criarAgendamento("AGD-011", "Isabela Costa", "12345678911", "11999999911", StatusAgendamentoEnum.CONFIRMADO_PACIENTE),
                criarAgendamento("AGD-012", "Thiago Alves", "12345678912", "11999999912", StatusAgendamentoEnum.AGUARDANDO_CONFIRMACAO),
                criarAgendamento("AGD-013", "Caroline Gomes", "12345678913", "11999999913", StatusAgendamentoEnum.CANCELADO),
                criarAgendamento("AGD-014", "Rodrigo Pereira", "12345678914", "11999999914", StatusAgendamentoEnum.CONFIRMADO_PACIENTE),
                criarAgendamento("AGD-015", "Paulina Dias", "12345678915", "11999999915", StatusAgendamentoEnum.AGUARDANDO_CONFIRMACAO),
                criarAgendamento("AGD-016", "Marcos Vieira", "12345678916", "11999999916", StatusAgendamentoEnum.CANCELADO),
                criarAgendamento("AGD-017", "Larissa Correia", "12345678917", "11999999917", StatusAgendamentoEnum.CONFIRMADO_PACIENTE),
                criarAgendamento("AGD-018", "Felipe Nascimento", "12345678918", "11999999918", StatusAgendamentoEnum.AGUARDANDO_CONFIRMACAO),
                criarAgendamento("AGD-019", "Vivian Mendes", "12345678919", "11999999919", StatusAgendamentoEnum.CANCELADO),
                criarAgendamento("AGD-020", "Sergio Martins", "12345678920", "11999999920", StatusAgendamentoEnum.CONFIRMADO_PACIENTE),
                criarAgendamento("AGD-021", "Tatiana Silva", "12345678921", "11999999921", StatusAgendamentoEnum.AGUARDANDO_CONFIRMACAO),
                criarAgendamento("AGD-022", "Ulisses Costa", "12345678922", "11999999922", StatusAgendamentoEnum.CANCELADO),
                criarAgendamento("AGD-023", "Vanessa Rocha", "12345678923", "11999999923", StatusAgendamentoEnum.CONFIRMADO_PACIENTE),
                criarAgendamento("AGD-024", "Wagner Gomes", "12345678924", "11999999924", StatusAgendamentoEnum.AGUARDANDO_CONFIRMACAO),
                criarAgendamento("AGD-025", "Xavier Pereira", "12345678925", "11999999925", StatusAgendamentoEnum.CANCELADO)
            );

            List<AgendamentoEntity> agendamentosSalvos = agendamentoJPARepository.saveAll(agendamentos);
            log.info("[DataSeeder] {} registros de teste inseridos com sucesso!", agendamentosSalvos.size());

            for (AgendamentoEntity ag : agendamentosSalvos) {
                AgendamentoPacienteEntity ap = AgendamentoPacienteEntity.builder()
                        .paciente(ag.getPaciente())
                        .agendamento(ag)
                        .dataRegistro(ag.getDataHora())
                        .status(ag.getStatus() != null ? ag.getStatus().toString() : StatusAgendamentoEnum.PENDENTE.toString())
                        .token(UUID.randomUUID().toString())
                        .build();
                agendamentoPacienteJPARepository.save(ap);
            }
        } else {
            log.info("[DataSeeder] Tabela agendamento já contém dados. Pulando população de testes.");
        }
    }

    private AgendamentoEntity criarAgendamento(String idExterno, String pacienteNome, String cpf, 
                                               String pacienteTelefone, StatusAgendamentoEnum status) {
        PacienteEntity pacienteSalvo = pacienteJPARepository.findById(cpf)
                .orElseGet(() -> {
                    PacienteEntity novoPaciente = PacienteEntity.builder()
                            .nome(pacienteNome)
                            .cpf(cpf)
                            .telefone(pacienteTelefone)
                            .email("jh93.dev@gmail.com")
                            .build();
                    return pacienteJPARepository.save(novoPaciente);
                });
        
        return AgendamentoEntity.builder()
                .idExterno(idExterno)
                .paciente(pacienteSalvo)
                .dataHora(LocalDateTime.of(2026, 2, 15, 10, 0))
                .medico("Dr. especialista")
                .especialidade("Cardiologia")
                .endereco("Endereço teste")
                .localAtendimento("Consultório")
                .unidadeId("UNI-001")
                .status(status)
                .dataLimiteConsulta(LocalDateTime.of(2026, 2, 14, 23, 59, 59))
                .build();
    }
}
