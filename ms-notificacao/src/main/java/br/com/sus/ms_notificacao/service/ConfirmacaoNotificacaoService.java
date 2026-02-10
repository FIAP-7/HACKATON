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

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfirmacaoNotificacaoService {

    private final JavaMailSender mailSender;

    public void enviarEmailAgendamento(ConfirmacaoNotificacaoRecord record) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String urlBase = "http://localhost:8080/api/v1/acao/confirmar?token=" + record.tokenUUID();

            String dataConsultaFormatada = DateFormatterUtil.formatarDataBrasileira(record.dataConsulta());

            String htmlContent = String.format("""
                <div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #ddd; padding: 20px; border-radius: 8px;'>
                    <div style='text-align: center; border-bottom: 2px solid #007bff; padding-bottom: 10px;'>
                        <h2 style='color: #007bff; margin: 0;'>SUS Ágil - Confirmação de Consulta</h2>
                        <p style='font-size: 14px; color: #555;'>Sua consulta está agendada!</p>
                    </div>
                    
                    <div style='padding: 20px 0;'>
                        <p>Olá, <strong>%s</strong>!</p>
                        <p>Sua consulta de <strong>%s</strong> está agendada.</p>
                        
                        <div style='background-color: #e8f4f8; padding: 15px; border-radius: 5px; border-left: 4px solid #007bff; margin-top: 15px;'>
                            <p style='margin: 5px 0;'><strong>📅 Data e Hora:</strong> %s</p>
                            <p style='margin: 5px 0;'><strong>🏥 Local:</strong> %s</p>
                            <p style='margin: 5px 0;'><strong>📍 Endereço:</strong> %s</p>
                        </div>
                        
                        <p style='color: #d9534f; font-size: 13px; margin-top: 15px; line-height: 1.6;'>
                            <strong>⚠️ Atenção:</strong> Por favor, confirme seu comparecimento ou cancele esta consulta. Se você não puder comparecer, é fundamental cancelar para que possamos oferecer esta vaga a outro paciente que aguarda atendimento. A ausência sem aviso prévio prejudica pacientes que precisam muito do atendimento no SUS.
                        </p>
                    </div>
                    
                    <div style='text-align: center; margin: 25px 0;'>
                        <a href='%s&acao=CONFIRMAR' style='background-color: #007bff; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; margin-right: 10px;'>Confirmar Presença</a>
                        <a href='%s&acao=CANCELAR' style='background-color: #dc3545; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold;'>Cancelar Consulta</a>
                    </div>
                    
                    <hr style='margin: 30px 0; border: 0; border-top: 1px solid #eee;'>
                    <p style='font-size: 12px; color: #777;'>Esta é uma mensagem automática do sistema SUS Ágil.</p>
                </div>
                """,
                    record.pacienteNome(), record.especialidade(), dataConsultaFormatada,
                    record.localAtendimento(), record.endereco(),
                    urlBase, urlBase
            );

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