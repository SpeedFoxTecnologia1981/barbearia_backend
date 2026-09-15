package br.com.barbearia.service;

import br.com.barbearia.entity.Agendamento;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificacaoServiceImpl implements NotificacaoService {
    @Override
    public void enviarConfirmacao(Agendamento agendamento) {
        log.info("Agendamento confirmado - Cliente: {}, Serviço: {}, Data: {}, Horário: {}",
            agendamento.getUsuario().getNome(),
            agendamento.getServico().getNome(),
            agendamento.getDataAgendamento(),
            agendamento.getHoraInicio());
    }
}
