package br.com.sus.ms_notificacao.service;

import br.com.sus.ms_notificacao.dto.ConfirmacaoAntecipacaoEvent;
import br.com.sus.ms_notificacao.util.DateFormatterUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfirmacaoAntecipacaoNotificacaoService {

    private final JavaMailSender mailSender;
    private final TemplateRenderer templateRenderer;

    public void enviarEmailConfirmacaoAntecipacao(ConfirmacaoAntecipacaoEvent event) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String dataConsultaFormatada = DateFormatterUtil.formatarDataBrasileira(event.novaDataHora().toString());

            Map<String, String> vars = new HashMap<>();
            vars.put("nome", event.paciente().nome());
            vars.put("especialidade", event.especialidade());
            vars.put("data", dataConsultaFormatada);
            vars.put("medico", event.medico());
            vars.put("local", event.localAtendimento());
            vars.put("endereco", event.endereco());

            String htmlContent = templateRenderer.render("templates/confirmacao-antecipacao.html", vars);

            helper.setTo(event.paciente().email());
            helper.setSubject("Confirmação de Antecipação de Consulta - SUS [" + event.especialidade() + "] - " + event.paciente().nome());
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("[ConfirmacaoAntecipacaoNotificacaoService] Email enviado com sucesso para paciente={}, email={}, nouvaDataHora={}",
                    event.paciente().nome(), event.paciente().email(), event.novaDataHora());
        } catch (MessagingException e) {
            log.error("[ConfirmacaoAntecipacaoNotificacaoService] Erro ao enviar email de confirmação de antecipação para paciente={}, email={}",
                    event.paciente().nome(), event.paciente().email(), e);
            throw new RuntimeException("Erro ao enviar email de confirmação de antecipação", e);
        }
    }
}
