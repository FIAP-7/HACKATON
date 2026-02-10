package br.com.sus.ms_processamento.infrastructure.config;

import br.com.sus.ms_processamento.domain.model.StatusAgendamentoEnum;
import br.com.sus.ms_processamento.infrastructure.persistence.entity.AgendamentoEntity;
import br.com.sus.ms_processamento.infrastructure.persistence.repository.AgendamentoJPARepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataSeederConfig implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeederConfig.class);

    private final AgendamentoJPARepository agendamentoJPARepository;

    public DataSeederConfig(AgendamentoJPARepository agendamentoJPARepository) {
        this.agendamentoJPARepository = agendamentoJPARepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (agendamentoJPARepository.count() == 0) {
            log.info("[DataSeeder] Iniciando população de dados de teste...");
            
            List<AgendamentoEntity> agendamentos = Arrays.asList(
                criarAgendamento("AGD-001", "João Silva", "11999999901", "token-001-abc", StatusAgendamentoEnum.AGUARDANDO_CONFIRMACAO),
                criarAgendamento("AGD-002", "Maria Santos", "11999999902", null, StatusAgendamentoEnum.CONFIRMADO_PACIENTE),
                criarAgendamento("AGD-003", "Pedro Costa", "11999999903", null, StatusAgendamentoEnum.CANCELADO),
                criarAgendamento("AGD-004", "Ana Oliveira", "11999999904", "token-004-def", StatusAgendamentoEnum.AGUARDANDO_CONFIRMACAO),
                criarAgendamento("AGD-005", "Lucas Ferreira", "11999999905", null, StatusAgendamentoEnum.CONFIRMADO_PACIENTE),
                criarAgendamento("AGD-006", "Carla Martins", "11999999906", "token-006-ghi", StatusAgendamentoEnum.AGUARDANDO_CONFIRMACAO),
                criarAgendamento("AGD-007", "Ricardo Souza", "11999999907", null, StatusAgendamentoEnum.CANCELADO),
                criarAgendamento("AGD-008", "Beatriz Lima", "11999999908", null, StatusAgendamentoEnum.CONFIRMADO_PACIENTE),
                criarAgendamento("AGD-009", "Gabriel Rocha", "11999999909", "token-009-jkl", StatusAgendamentoEnum.AGUARDANDO_CONFIRMACAO),
                criarAgendamento("AGD-010", "Fernanda Santos", "11999999910", null, StatusAgendamentoEnum.CANCELADO),
                criarAgendamento("AGD-011", "Isabela Costa", "11999999911", null, StatusAgendamentoEnum.CONFIRMADO_PACIENTE),
                criarAgendamento("AGD-012", "Thiago Alves", "11999999912", "token-012-mno", StatusAgendamentoEnum.AGUARDANDO_CONFIRMACAO),
                criarAgendamento("AGD-013", "Caroline Gomes", "11999999913", null, StatusAgendamentoEnum.CANCELADO),
                criarAgendamento("AGD-014", "Rodrigo Pereira", "11999999914", null, StatusAgendamentoEnum.CONFIRMADO_PACIENTE),
                criarAgendamento("AGD-015", "Paulina Dias", "11999999915", "token-015-pqr", StatusAgendamentoEnum.AGUARDANDO_CONFIRMACAO),
                criarAgendamento("AGD-016", "Marcos Vieira", "11999999916", null, StatusAgendamentoEnum.CANCELADO),
                criarAgendamento("AGD-017", "Larissa Correia", "11999999917", null, StatusAgendamentoEnum.CONFIRMADO_PACIENTE),
                criarAgendamento("AGD-018", "Felipe Nascimento", "11999999918", "token-018-stu", StatusAgendamentoEnum.AGUARDANDO_CONFIRMACAO),
                criarAgendamento("AGD-019", "Vivian Mendes", "11999999919", null, StatusAgendamentoEnum.CANCELADO),
                criarAgendamento("AGD-020", "Sergio Martins", "11999999920", null, StatusAgendamentoEnum.CONFIRMADO_PACIENTE),
                criarAgendamento("AGD-021", "Tatiana Silva", "11999999921", "token-021-vwx", StatusAgendamentoEnum.AGUARDANDO_CONFIRMACAO),
                criarAgendamento("AGD-022", "Ulisses Costa", "11999999922", null, StatusAgendamentoEnum.CANCELADO),
                criarAgendamento("AGD-023", "Vanessa Rocha", "11999999923", null, StatusAgendamentoEnum.CONFIRMADO_PACIENTE),
                criarAgendamento("AGD-024", "Wagner Gomes", "11999999924", "token-024-yza", StatusAgendamentoEnum.AGUARDANDO_CONFIRMACAO),
                criarAgendamento("AGD-025", "Xavier Pereira", "11999999925", null, StatusAgendamentoEnum.CANCELADO)
            );

            agendamentoJPARepository.saveAll(agendamentos);
            log.info("[DataSeeder] {} registros de teste inseridos com sucesso!", agendamentos.size());
        } else {
            log.info("[DataSeeder] Tabela agendamento já contém dados. Pulando população de testes.");
        }
    }

    private AgendamentoEntity criarAgendamento(String idExterno, String pacienteNome, String pacienteTelefone,
                                               String tokenUUID, StatusAgendamentoEnum status) {
        return AgendamentoEntity.builder()
                .idExterno(idExterno)
                .pacienteNome(pacienteNome)
                .pacienteTelefone(pacienteTelefone)
                .pacienteEmail("jh93.dev@gmail.com")
                .dataHora(LocalDateTime.of(2026, 2, 15, 10, 0))
                .medico("Dr. especialista")
                .especialidade("Cardiologia")
                .endereco("Endereço teste")
                .localAtendimento("Consultório")
                .unidadeId("UNI-001")
                .status(status)
                .dataLimiteConsulta(LocalDateTime.of(2026, 2, 14, 23, 59, 59))
                .tokenUUID(tokenUUID)
                .build();
    }
}
