package br.com.barbearia.entity;

import br.com.barbearia.enums.StatusAgendamento;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "agendamento")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Agendamento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barbeiro_id", nullable = false)
    private Barbeiro barbeiro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;

    @Column(nullable = false)
    private LocalDate dataAgendamento;

    @Column(nullable = false)
    private LocalTime horaInicio;

    @Column(nullable = false)
    private LocalTime horaFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAgendamento status;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    public void prePersist() {
        if (status == null) status = StatusAgendamento.CONFIRMADO;
        if (dataCriacao == null) dataCriacao = LocalDateTime.now();
    }
}
