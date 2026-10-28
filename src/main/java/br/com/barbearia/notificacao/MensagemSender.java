package br.com.barbearia.notificacao;

/** Contrato de envio. Trocar de provedor = criar outra implementação. */
public interface MensagemSender {
    void enviar(MensagemNotificacao mensagem);
}
