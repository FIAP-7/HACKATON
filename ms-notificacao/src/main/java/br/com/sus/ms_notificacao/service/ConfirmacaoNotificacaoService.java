package br.com.sus.ms_notificacao.service;

import br.com.sus.ms_notificacao.dto.ConfirmacaoNotificacaoRecord;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfirmacaoNotificacaoService {

    private final JavaMailSender mailSender;

    /**
     * Envia um e-mail formatado em HTML com links interativos para o paciente.
     */
    public void enviarEmailAgendamento(ConfirmacaoNotificacaoRecord record) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // No Hackathon, esses links apontariam para o seu Front-end React ou seu API Gateway
            String urlBase = "http://localhost:8080/v1/agendamentos/acao?id=" + record.pacienteNome().hashCode(); // Exemplo de ID

            String htmlContent = String.format(
                    "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #ddd; padding: 20px;'>" +
                            "  <h2 style='color: #007bff;'>SUS Ágil - Notificação de Consulta</h2>" +
                            "  <p>Olá, <strong>%s</strong>!</p>" +
                            "  <p>Sua consulta foi agendada para: <span style='color: #d9534f;'>%s</span></p>" +
                            "  <p>Por favor, selecione uma das opções abaixo para gerenciar seu agendamento:</p>" +
                            "  <div style='margin-top: 20px; text-align: center;'>" +
                            "    <a href='%s&tipo=CONFIRMAR' style='background-color: #28a745; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; margin-right: 10px;'>Confirmar</a>" +
                            "    <a href='%s&tipo=CANCELAR' style='background-color: #dc3545; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; margin-right: 10px;'>Cancelar</a>" +
                            "    <a href='%s&tipo=REAGENDAR' style='background-color: #ffc107; color: black; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Reagendar</a>" +
                            "  </div>" +
                            "  <hr style='margin-top: 30px; border: 0; border-top: 1px solid #eee;'>" +
                            "  <p style='font-size: 12px; color: #777;'>Esta é uma mensagem automática do sistema SUS Ágil.</p>" +
                            "</div>",
                    record.pacienteNome(), record.dataConsulta(), urlBase, urlBase, urlBase
            );

            helper.setFrom("nao-responda@susagil.com.br");
            helper.setTo(record.pacienteEmail());
            helper.setSubject("Confirmação de Agendamento - " + record.pacienteNome());
            helper.setText(htmlContent, true); // O 'true' indica que o conteúdo é HTML

            mailSender.send(message);
            log.info("E-mail de notificação enviado com sucesso para: {}", record.pacienteEmail());

        } catch (MessagingException e) {
            log.error("Falha ao montar o e-mail para {}: {}", record.pacienteEmail(), e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao enviar e-mail: {}", e.getMessage());
        }
    }
}