package br.com.sus.ingestao.entrypoint.controller;

import br.com.sus.ingestao.core.usecase.IngestaoService;
import br.com.sus.ingestao.entrypoint.dto.AgendamentoRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/integracao")
public class AgendamentoController {

    private final IngestaoService ingestaoService;

    public AgendamentoController(IngestaoService ingestaoService) {
        this.ingestaoService = ingestaoService;
    }

    @PostMapping("/agendamentos")
    public ResponseEntity<Void> receberAgendamento(@Valid @RequestBody AgendamentoRequest request) {
        ingestaoService.processarAgendamento(request);
        return ResponseEntity.accepted().build();
    }
}
