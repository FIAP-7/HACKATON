package br.com.sus.ingestao.entrypoint.controller;

import br.com.sus.ingestao.core.usecase.IngestaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/acao")
@Tag(name = "Ação por Email", description = "Recebimento de cliques em links de e-mail (magic link)")
public class AcaoEmailController {

    private final IngestaoService ingestaoService;

    public AcaoEmailController(IngestaoService ingestaoService) {
        this.ingestaoService = ingestaoService;
    }

    @GetMapping(value = "/confirmar", produces = MediaType.TEXT_HTML_VALUE)
    @Operation(summary = "Recebe ação do usuário via e-mail (GET)", description = "Endpoint público para registrar a ação enviada por e-mail através de token")
    public ResponseEntity<String> confirmar(
            @Parameter(description = "Token único do e-mail", required = true)
            @RequestParam("token") String token,
            @Parameter(description = "Ação do usuário (ex.: CONFIRMAR)", required = true)
            @RequestParam("acao") String acao
    ) {
        ingestaoService.processarAcaoEmail(token, acao);
        String html = "<html><body><h1>Confirmado</h1><p>Sua resposta foi registrada com sucesso.</p></body></html>";
        return ResponseEntity.ok(html);
    }

    @GetMapping(value = "/antecipar", produces = MediaType.TEXT_HTML_VALUE)
    @Operation(summary = "Recebe ação do usuário via e-mail (GET)", description = "Endpoint público para registrar a ação enviada por e-mail através de token")
    public ResponseEntity<String> antecipar(
            @Parameter(description = "Token único do e-mail", required = true)
            @RequestParam("token") String token,
            @Parameter(description = "Ação do usuário (ex.: CONFIRMAR)", required = true)
            @RequestParam("acao") String acao
    ) {
        ingestaoService.processarAntecipacaoEmail(token, acao);
        String html = "<html><body><h1>Confirmado</h1><p>Sua resposta foi registrada com sucesso.</p></body></html>";
        return ResponseEntity.ok(html);
    }
}
