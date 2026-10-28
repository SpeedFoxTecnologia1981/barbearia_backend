package br.com.barbearia.notificacao;

import java.util.List;

/**
 * Mensagem já montada, sem dependência de entidades JPA (pode ser enviada em outra thread).
 *
 * @param telefone  somente dígitos, com DDI (ex.: 5531980208758)
 * @param texto     texto livre (SMS / WhatsApp sandbox ou dentro da janela de 24h)
 * @param variaveis valores na ordem {{1}}, {{2}}... para templates aprovados do WhatsApp
 */
public record MensagemNotificacao(String telefone, String texto, List<String> variaveis) {}
