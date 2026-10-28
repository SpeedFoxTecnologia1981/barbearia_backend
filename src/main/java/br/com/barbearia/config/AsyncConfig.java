package br.com.barbearia.config;

import br.com.barbearia.notificacao.NotificacaoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/** Habilita @Async (envio de mensagens em segundo plano) e as propriedades de notificação. */
@Configuration
@EnableAsync
@EnableConfigurationProperties(NotificacaoProperties.class)
public class AsyncConfig {
}
