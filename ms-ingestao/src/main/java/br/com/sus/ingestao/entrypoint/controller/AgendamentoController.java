package br.com.sus.ingestao.entrypoint.controller;

import br.com.sus.ingestao.core.usecase.IngestaoService;
import br.com.sus.ingestao.core.usecase.model.AgendamentoCommand;
import br.com.sus.ingestao.entrypoint.dto.AgendamentoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/integracao")
@Tag(name = "Integração", description = "Endpoints para ingestão de agendamentos vindos do sistema legado")
public class AgendamentoController {

    private final IngestaoService ingestaoService;

    public AgendamentoController(IngestaoService ingestaoService) {
        this.ingestaoService = ingestaoService;
    }

    @PostMapping("/agendamentos")
    @PreAuthorize("hasRole('INTEGRADOR')")
    @Operation(
            summary = "Recebe carga de agendamentos",
            description = "Recebe um JSON com dados do agendamento, valida e publica evento para processamento assíncrono.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Agendamento recebido e aceito para processamento"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sem permissão (role ausente)", content = @Content)
    })
    public ResponseEntity<Void> receberAgendamento(
            @Valid
            @RequestBody(required = true, description = "Payload do agendamento",
                    content = @Content(schema = @Schema(implementation = AgendamentoRequest.class)))
            @org.springframework.web.bind.annotation.RequestBody AgendamentoRequest request) {
        AgendamentoCommand command = new AgendamentoCommand(
                request.idExterno(),
                new AgendamentoCommand.Paciente(request.paciente().nome(), request.paciente().telefone()),
                new AgendamentoCommand.Consulta(
                        request.consulta().dataHora(),
                        request.consulta().medico(),
                        request.consulta().especialidade(),
                        request.consulta().unidadeId()
                )
        );
        ingestaoService.processarAgendamento(command);
        return ResponseEntity.accepted().build();
    }
}
