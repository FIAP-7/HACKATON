package br.com.sus.ms_notificacao.service;

import br.com.sus.ms_notificacao.dto.AntecipacaoNotificacaoRecord;
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
public class AntecipacaoNotificacaoService {

    private final JavaMailSender mailSender;
    private final TemplateRenderer templateRenderer;

    public void enviarEmailAntecipacao(AntecipacaoNotificacaoRecord record) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String urlBase = "http://localhost:8080/api/v1/acao/antecipar?token=" + record.tokenUUID();

            String dataAgendadaFormatada = DateFormatterUtil.formatarDataBrasileira(record.dataHoraAgendada());
            String dataAntecipacaoFormatada = DateFormatterUtil.formatarDataBrasileira(record.dataHoraConsultaAntecipada());

            Map<String, String> vars = new HashMap<>();
            vars.put("nome", record.nomePacienteAntecipacao());
            vars.put("especialidade", record.especialidadeAgendada());
            vars.put("dataAgendada", dataAgendadaFormatada);
            vars.put("dataAntecipada", dataAntecipacaoFormatada);
            vars.put("local", record.localAtendimentoConsultaAntecipada());
            vars.put("endereco", record.enderecoConsultaAntecipada());
            vars.put("url", urlBase);

            String htmlContent = templateRenderer.render("templates/antecipacao.html", vars);

            helper.setTo(record.emailPacienteAntecipacao());
            helper.setSubject("Oportunidade: Antecipe sua consulta de " + record.especialidadeAgendada());
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("E-mail de antecipação enviado: {}", record.emailPacienteAntecipacao());

        } catch (MessagingException e) {
            log.error("Erro ao enviar antecipação: {}", e.getMessage());
        }
    }
}