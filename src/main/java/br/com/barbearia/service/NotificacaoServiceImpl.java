package br.com.barbearia.service;

import br.com.barbearia.entity.Agendamento;
import br.com.barbearia.notificacao.MensagemNotificacao;
import br.com.barbearia.notificacao.MensagemSender;
import br.com.barbearia.notificacao.NotificacaoProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificacaoServiceImpl implements NotificacaoService {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", PT_BR);
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final MensagemSender mensagemSender;
    private final NotificacaoProperties props;

    @Override
    public void enviarConfirmacao(Agendamento a) {
        log.info("Agendamento confirmado - Cliente: {}, Serviço: {}, Data: {}, Horário: {}",
            a.getUsuario().getNome(), a.getServico().getNome(),
            a.getDataAgendamento(), a.getHoraInicio());

        // A mensagem é montada AGORA (dentro da transação, com as entidades carregadas)
        // e enviada somente após o commit, para não avisar o cliente de algo que sofreu rollback.
        MensagemNotificacao mensagem;
        try {
            mensagem = montarMensagem(a);
        } catch (Exception e) {
            log.error("Não foi possível montar a notificação do agendamento {}: {}", a.getId(), e.getMessage());
            return;
        }

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    mensagemSender.enviar(mensagem);
                }
            });
        } else {
            mensagemSender.enviar(mensagem);
        }
    }

    private MensagemNotificacao montarMensagem(Agendamento a) {
        String nomeCliente = primeiroNome(a.getUsuario().getNome());
        String servico = a.getServico().getNome();
        String barbeiro = a.getBarbeiro().getNome();
        String data = capitalizar(a.getDataAgendamento().format(DATA));
        String hora = a.getHoraInicio().format(HORA);
        String valor = NumberFormat.getCurrencyInstance(PT_BR).format(a.getServico().getValor());

        String texto = """
            ✅ %s: agendamento confirmado!

            Olá, %s. Seu horário está reservado:
            ✂️ Serviço: %s
            💈 Barbeiro: %s
            📅 %s
            🕒 %s
            💰 %s

            Para cancelar, acesse "Meus agendamentos" no site (até 1h antes).""".formatted(
                props.nomeBarbearia(), nomeCliente, servico, barbeiro, data, hora, valor);

        // Ordem das variáveis do template aprovado no WhatsApp: {{1}}..{{6}}
        List<String> variaveis = List.of(nomeCliente, servico, barbeiro, data, hora, valor);

        return new MensagemNotificacao(normalizarTelefone(a.getUsuario().getTelefone()), texto, variaveis);
    }

    /** Telefone salvo só com dígitos e DDD (ex.: 31980208758) -> adiciona DDI do Brasil (55). */
    static String normalizarTelefone(String telefone) {
        String d = telefone.replaceAll("\\D", "");
        if ((d.length() == 12 || d.length() == 13) && d.startsWith("55")) return d;
        return "55" + d;
    }

    private static String primeiroNome(String nome) {
        return nome.trim().split("\\s+")[0];
    }

    private static String capitalizar(String s) {
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
