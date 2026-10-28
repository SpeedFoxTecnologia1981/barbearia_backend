package br.com.barbearia.notificacao;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configurações de notificação (prefixo "notificacao" no application.yml).
 *
 * canal: WHATSAPP ou SMS.
 * Para WhatsApp em produção, informe o templateSid (Content SID aprovado na Twilio),
 * pois mensagens iniciadas pela empresa fora da janela de 24h exigem template.
 */
@ConfigurationProperties(prefix = "notificacao")
public record NotificacaoProperties(
        boolean habilitado,
        Canal canal,
        String nomeBarbearia,
        Twilio twilio
) {
    public enum Canal { WHATSAPP, SMS }

    public record Twilio(
            String accountSid,
            String authToken,
            /** Número remetente. WhatsApp: "+14155238886" (sandbox) ou número aprovado. SMS: número Twilio. */
            String from,
            /** Opcional: Content SID (HX...) do template aprovado para WhatsApp. */
            String templateSid
    ) {}
}
