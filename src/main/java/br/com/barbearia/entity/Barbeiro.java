package br.com.barbearia.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "barbeiro")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Barbeiro {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 150)
    private String nome;
    @Column(length = 20)
    private String telefone;
    @Column(nullable = false)
    private Boolean ativo = true;
}
