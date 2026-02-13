package br.com.sus.ms_notificacao.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatterUtil {

    private static final DateTimeFormatter FORMATTER_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private DateFormatterUtil() {
    }

    public static String formatarDataBrasileira(String dataISO) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dataISO);
            return dateTime.format(FORMATTER_BR);
        } catch (Exception e) {
            return dataISO;
        }
    }
}
