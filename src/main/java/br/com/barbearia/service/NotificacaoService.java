package br.com.barbearia.service;
import br.com.barbearia.entity.Agendamento;
public interface NotificacaoService {
    void enviarConfirmacao(Agendamento agendamento);
}
