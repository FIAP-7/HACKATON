package br.com.sus.ingestao.entrypoint.controller;

import br.com.sus.ingestao.core.usecase.IngestaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/webhook")
@Tag(name = "Webhook", description = "Recebimento de respostas do WhatsApp (Twilio)")
public class WebhookController {

    private final IngestaoService ingestaoService;

    public WebhookController(IngestaoService ingestaoService) {
        this.ingestaoService = ingestaoService;
    }

    @PostMapping(value = "/twilio", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Operation(
            summary = "Recebe webhook do Twilio",
            description = "Recebe o POST form-urlencoded com os campos 'From' e 'Body', higieniza o telefone e publica evento para processamento.",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recebido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado (Basic)", content = @Content)
    })
    public ResponseEntity<Void> receberTwilio(
            @Parameter(description = "Origem do WhatsApp no formato 'whatsapp:+5511999998888'", example = "whatsapp:+5511999998888")
            @RequestParam(name = "From", required = false) String from,
            @Parameter(description = "Conteúdo da mensagem enviada pelo usuário", example = "SIM")
            @RequestParam(name = "Body", required = false) String body) {
        ingestaoService.processarRespostaUsuario(from, body);
        return ResponseEntity.ok().build();
    }
}
