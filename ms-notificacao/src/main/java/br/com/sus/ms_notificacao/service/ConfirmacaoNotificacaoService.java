package br.com.sus.ms_notificacao.service;

import br.com.sus.ms_notificacao.dto.ConfirmacaoNotificacaoRecord;
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
public class ConfirmacaoNotificacaoService {

    private final JavaMailSender mailSender;
    private final TemplateRenderer templateRenderer;

    public void enviarEmailAgendamento(ConfirmacaoNotificacaoRecord record) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String urlBase = "http://localhost:8080/api/v1/acao/confirmar?token=" + record.tokenUUID();

            String dataConsultaFormatada = DateFormatterUtil.formatarDataBrasileira(record.dataConsulta());

            Map<String, String> vars = new HashMap<>();
            vars.put("nome", record.pacienteNome());
            vars.put("especialidade", record.especialidade());
            vars.put("data", dataConsultaFormatada);
            vars.put("local", record.localAtendimento());
            vars.put("endereco", record.endereco());
            vars.put("url", urlBase);

            String htmlContent = templateRenderer.render("templates/confirmacao.html", vars);

            helper.setFrom("nao-responda@susagil.com.br");
            helper.setTo(record.pacienteEmail());
            helper.setSubject("Confirmação de Agendamento - " + record.pacienteNome());
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("E-mail de notificação enviado com sucesso para: {}", record.pacienteEmail());

        } catch (MessagingException e) {
            log.error("Falha ao montar o e-mail para {}: {}", record.pacienteEmail(), e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao enviar e-mail: {}", e.getMessage());
        }
    }
}