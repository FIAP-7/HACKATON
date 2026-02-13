package br.com.sus.ingestao.entrypoint.controller;

import br.com.sus.ingestao.core.usecase.IngestaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/v1/acao")
@Tag(name = "Ação por Email", description = "Recebimento de cliques em links de e-mail (magic link)")
public class AcaoEmailController {

    private final IngestaoService ingestaoService;

    public AcaoEmailController(IngestaoService ingestaoService) {
        this.ingestaoService = ingestaoService;
    }

    @GetMapping(value = "/confirmar", produces = MediaType.TEXT_HTML_VALUE)
    @Operation(summary = "Recebe ação do usuário via e-mail (GET)", description = "Endpoint público para registrar a ação enviada por e-mail através de token")
    public String confirmar(
            @Parameter(description = "Token único do e-mail", required = true)
            @RequestParam("token") String token,
            @Parameter(description = "Ação do usuário (ex.: CONFIRMAR, CANCELAR)", required = true)
            @RequestParam("acao") String acao,
            Model model
    ) {
        ingestaoService.processarAcaoEmail(token, acao);
        model.addAttribute("acao", descricaoAcao(acao));
        return "confirmacao-resposta";
    }

    @GetMapping(value = "/antecipar", produces = MediaType.TEXT_HTML_VALUE)
    @Operation(summary = "Recebe ação do usuário via e-mail (GET)", description = "Endpoint público para registrar a ação enviada por e-mail através de token")
    public String antecipar(
            @Parameter(description = "Token único do e-mail", required = true)
            @RequestParam("token") String token,
            @Parameter(description = "Ação do usuário (ex.: ACEITAR, MANTER)", required = true)
            @RequestParam("acao") String acao,
            Model model
    ) {
        ingestaoService.processarAntecipacaoEmail(token, acao);
        
        if ("ACEITAR".equalsIgnoreCase(acao)) {
            return "validacao";
        }
        
        model.addAttribute("acao", descricaoAcao(acao));
        return "confirmacao-resposta";
    }

    private String descricaoAcao(String acao) {
        return switch (acao.toUpperCase()) {
            case "CONFIRMAR" -> "confirmado";
            case "CANCELAR" -> "cancelado";
            case "MANTER" -> "mantido";
            default -> acao.toLowerCase();
        };
    }
}
