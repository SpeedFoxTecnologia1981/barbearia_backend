package br.com.barbearia.notificacao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Envia SMS ou WhatsApp pela API REST da Twilio (sem SDK, usando RestClient).
 * Roda em thread separada (@Async): falha no envio nunca derruba o agendamento.
 */
@Component
@Slf4j
@ConditionalOnProperty(prefix = "notificacao", name = "habilitado", havingValue = "true")
public class TwilioMensagemSender implements MensagemSender {

    private final NotificacaoProperties props;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public TwilioMensagemSender(NotificacaoProperties props, ObjectMapper objectMapper) {
        this.props = props;
        this.objectMapper = objectMapper;
        NotificacaoProperties.Twilio t = props.twilio();
        if (t == null || isBlank(t.accountSid()) || isBlank(t.authToken()) || isBlank(t.from())) {
            throw new IllegalStateException(
                "notificacao.habilitado=true, mas faltam notificacao.twilio.account-sid / auth-token / from");
        }
        this.restClient = RestClient.builder()
            .baseUrl("https://api.twilio.com/2010-04-01/Accounts/" + t.accountSid())
            .defaultHeaders(h -> h.setBasicAuth(t.accountSid(), t.authToken()))
            .build();
    }

    @Async
    @Override
    public void enviar(MensagemNotificacao msg) {
        try {
            boolean whatsapp = props.canal() == NotificacaoProperties.Canal.WHATSAPP;
            String prefixo = whatsapp ? "whatsapp:" : "";

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("To", prefixo + "+" + msg.telefone());
            form.add("From", prefixo + normalizarRemetente(props.twilio().from()));

            String templateSid = props.twilio().templateSid();
            if (whatsapp && !isBlank(templateSid)) {
                form.add("ContentSid", templateSid);
                form.add("ContentVariables", variaveisJson(msg));
            } else {
                form.add("Body", msg.texto());
            }

            restClient.post()
                .uri("/Messages.json")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(form)
                .retrieve()
                .toBodilessEntity();

            log.info("Notificação ({}) enviada para {}", props.canal(), mascarar(msg.telefone()));
        } catch (Exception e) {
            // Não propaga: o agendamento já foi confirmado e salvo.
            log.error("Falha ao enviar notificação ({}) para {}: {}",
                props.canal(), mascarar(msg.telefone()), e.getMessage());
        }
    }

    private String variaveisJson(MensagemNotificacao msg) throws JsonProcessingException {
        Map<String, String> vars = new LinkedHashMap<>();
        for (int i = 0; i < msg.variaveis().size(); i++) {
            vars.put(String.valueOf(i + 1), msg.variaveis().get(i));
        }
        return objectMapper.writeValueAsString(vars);
    }

    private static String normalizarRemetente(String from) {
        String digitos = from.replaceAll("\\D", "");
        return "+" + digitos;
    }

    private static String mascarar(String telefone) {
        return telefone.length() <= 4 ? "****" : "****" + telefone.substring(telefone.length() - 4);
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
