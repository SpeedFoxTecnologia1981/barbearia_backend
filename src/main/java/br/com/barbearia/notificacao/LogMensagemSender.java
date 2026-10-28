package br.com.barbearia.notificacao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Usado quando notificacao.habilitado=false (desenvolvimento): apenas registra no log. */
@Component
@Slf4j
@ConditionalOnProperty(prefix = "notificacao", name = "habilitado", havingValue = "false", matchIfMissing = true)
public class LogMensagemSender implements MensagemSender {
    @Override
    public void enviar(MensagemNotificacao mensagem) {
        log.info("[Notificação DESABILITADA] Para {}:\n{}", mensagem.telefone(), mensagem.texto());
    }
}
