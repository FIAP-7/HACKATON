package br.com.sus.ms_notificacao.service;

import br.com.sus.ms_notificacao.dto.AntecipacaoNotificacaoRecord;
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
public class AntecipacaoNotificacaoService {

    private final JavaMailSender mailSender;

    public void enviarEmailAntecipacao(AntecipacaoNotificacaoRecord record) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String urlBase = "http://localhost:8080/v1/notificacoes/antecipar/" + record.tokenUUID();

            String htmlContent = String.format("""
                <div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #ddd; padding: 20px; border-radius: 8px;'>
                    <div style='text-align: center; border-bottom: 2px solid #28a745; padding-bottom: 10px;'>
                        <h2 style='color: #28a745; margin: 0;'>Oportunidade de Antecipação</h2>
                        <p style='font-size: 14px; color: #555;'>Uma vaga mais próxima ficou disponível!</p>
                    </div>
                    
                    <div style='padding: 20px 0;'>
                        <p>Olá, <strong>%s</strong>!</p>
                        <p>Identificamos uma vaga para <strong>%s</strong> antes do seu horário agendado.</p>
                        
                        <div style='display: flex; justify-content: space-between; gap: 10px; margin-top: 20px;'>
                            <div style='flex: 1; background-color: #f8f9fa; padding: 10px; border-radius: 5px; border: 1px solid #dee2e6;'>
                                <p style='margin: 0; color: #666; font-size: 12px;'>AGENDAMENTO ATUAL</p>
                                <p style='margin: 5px 0;'><strong>📅 %s</strong></p>
                            </div>
                            <div style='flex: 1; background-color: #e8f5e9; padding: 10px; border-radius: 5px; border: 1px solid #c8e6c9;'>
                                <p style='margin: 0; color: #2e7d32; font-size: 12px;'>NOVA DATA DISPONÍVEL</p>
                                <p style='margin: 5px 0;'><strong>📅 %s</strong></p>
                            </div>
                        </div>

                        <div style='margin-top: 15px; padding: 10px; background-color: #fff3e0; border-radius: 5px;'>
                            <p style='margin: 0; font-size: 13px;'><strong>Local:</strong> %s</p>
                            <p style='margin: 0; font-size: 13px;'><strong>Endereço:</strong> %s</p>
                        </div>
                        
                        <p style='color: #d32f2f; font-size: 13px; margin-top: 15px;'>
                            * Esta vaga será preenchida pelo primeiro paciente que confirmar.
                        </p>
                    </div>
                    
                    <div style='text-align: center; margin: 25px 0;'>
                        <a href='%s?acao=ACEITAR' style='background-color: #28a745; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold;'>ACEITAR ANTECIPAÇÃO</a>
                        <p style='margin-top: 15px;'>
                            <a href='%s?acao=MANTER' style='color: #666; text-decoration: underline; font-size: 14px;'>Manter meu horário atual</a>
                        </p>
                    </div>
                </div>
                """,
                    record.nomePacienteAntecipacao(), record.especialidadeAgendada(), record.dataHoraAgendada(),
                    record.dataHoraConsultaAntecipada(), record.localAtendimentoConsultaAntecipada(), record.enderecoConsultaAntecipada(),
                    urlBase, urlBase
            );  

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