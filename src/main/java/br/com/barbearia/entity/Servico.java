package br.com.barbearia.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "servico")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Servico {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String nome;
    private String descricao;
    @Column(nullable = false)
    private Integer duracaoMinutos;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
    @Column(nullable = false)
    private Boolean ativo = true;
}
