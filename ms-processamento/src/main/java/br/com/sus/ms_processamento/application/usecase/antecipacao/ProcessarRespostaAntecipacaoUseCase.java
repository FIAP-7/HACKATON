package br.com.sus.ms_processamento.application.usecase.antecipacao;

import br.com.sus.ms_processamento.application.gateway.IAntecipacaoGateway;
import br.com.sus.ms_processamento.domain.model.RespostaPacienteEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProcessarRespostaAntecipacaoUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessarRespostaAntecipacaoUseCase.class);

    private final IAntecipacaoGateway antecipacaoGateway;

    public ProcessarRespostaAntecipacaoUseCase(IAntecipacaoGateway antecipacaoGateway) {
        this.antecipacaoGateway = antecipacaoGateway;
    }

    public void executar(String token, String resposta) {
        if (token == null || token.isBlank()) {
            log.warn("[ProcessarRespostaAntecipacaoUseCase] Token inválido. Descartando.");
            return;
        }

        if (RespostaPacienteEnum.ACEITAR.name().equalsIgnoreCase(resposta)) {
            antecipacaoGateway.confirmarAntecipacao(token);
        } else if (RespostaPacienteEnum.MANTER.name().equalsIgnoreCase(resposta)) {
            antecipacaoGateway.recusarAntecipacao(token);
        } else {
            log.info("[ProcessarRespostaAntecipacaoUseCase] Resposta desconhecida='{}'. Nenhuma ação aplicada.", resposta);
        }
    }
}
