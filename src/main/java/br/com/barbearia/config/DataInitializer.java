package br.com.barbearia.config;

import br.com.barbearia.entity.Barbeiro;
import br.com.barbearia.entity.Servico;
import br.com.barbearia.repository.BarbeiroRepository;
import br.com.barbearia.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/** Cadastra o barbeiro e os serviços iniciais quando as tabelas estão vazias. */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final BarbeiroRepository barbeiroRepository;
    private final ServicoRepository servicoRepository;

    @Override
    public void run(String... args) {
        if (barbeiroRepository.count() == 0) {
            barbeiroRepository.save(Barbeiro.builder()
                .nome("Julius").telefone("31980208758").ativo(true).build());
        }
        if (servicoRepository.count() == 0) {
            servicoRepository.saveAll(List.of(
                Servico.builder().nome("Corte Fade & Degradê Cirúrgico")
                    .descricao("Acabamento minucioso com tesoura e máquina, finalização com pomada matte premium e alinhamento do pezinho.")
                    .duracaoMinutos(45).valor(new BigDecimal("55.00")).ativo(true).build(),
                Servico.builder().nome("Combo Imperial (Cabelo + Barba Completa)")
                    .descricao("O combo mais desejado: corte estilizado completo com alinhamento minucioso e acabamento impecável da barba.")
                    .duracaoMinutos(75).valor(new BigDecimal("90.00")).ativo(true).build(),
                Servico.builder().nome("Corte Clássico na Tesoura")
                    .descricao("Técnica tradicional executada 100% na tesoura, respeitando o caimento e alinhamento natural dos fios.")
                    .duracaoMinutos(45).valor(new BigDecimal("50.00")).ativo(true).build()
            ));
        }
    }
}
