package io.github.msimeaor.aplicacao.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "servico")
public class Servico {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;

  @Column(name = "nome", length = 100, unique = true, nullable = false)
  private String nome;

  @Column(name = "valor", precision = 8, scale = 2, nullable = false)
  private BigDecimal valor;

  @ManyToMany(mappedBy = "servicos")
  private List<Venda> vendas;

}
