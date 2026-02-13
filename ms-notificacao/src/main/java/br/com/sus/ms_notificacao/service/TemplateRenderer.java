package br.com.sus.ms_notificacao.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class TemplateRenderer {

    public String render(String templatePath, Map<String, String> vars) {
        try (InputStream is = new ClassPathResource(templatePath).getInputStream()) {
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            if (vars != null) {
                for (Map.Entry<String, String> e : vars.entrySet()) {
                    String key = "{{" + e.getKey() + "}}";
                    content = content.replace(key, e.getValue() == null ? "" : e.getValue());
                }
            }
            return content;
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao carregar template: " + templatePath, ex);
        }
    }
}
