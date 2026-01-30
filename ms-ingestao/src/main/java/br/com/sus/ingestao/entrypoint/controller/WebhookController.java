package br.com.sus.ingestao.entrypoint.controller;

import br.com.sus.ingestao.core.usecase.IngestaoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/webhook")
public class WebhookController {

    private final IngestaoService ingestaoService;

    public WebhookController(IngestaoService ingestaoService) {
        this.ingestaoService = ingestaoService;
    }

    @PostMapping(value = "/twilio", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> receberTwilio(@RequestParam(name = "From", required = false) String from,
                                              @RequestParam(name = "Body", required = false) String body) {
        ingestaoService.processarRespostaUsuario(from, body);
        return ResponseEntity.ok().build();
    }
}
