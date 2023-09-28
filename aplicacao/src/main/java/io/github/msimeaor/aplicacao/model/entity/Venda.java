package io.github.msimeaor.aplicacao.model.entity;

import io.github.msimeaor.aplicacao.enums.FormasPagamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "venda")
public class Venda {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;

  @Column(name = "data_inicio", nullable = false)
  private LocalDate dataInicio;

  @Column(name = "data_entrega", nullable = false)
  private LocalDate dataEntrega;

  @Enumerated(EnumType.STRING)
  @Column(name = "forma_pagamento", nullable = false)
  private FormasPagamento formaPagamento;

  @Column(name = "qtd_parcelas")
  private int qtdParcelas;

  @Column(name = "valor_total", precision = 8, scale = 2)
  private BigDecimal valorTotal;

  @Column(name = "desconto", precision = 8, scale = 2)
  private BigDecimal desconto;

  @ManyToOne
  @JoinColumn(name = "pessoa")
  private Pessoa pessoa;

  @ManyToMany
  @JoinTable(
    name = "venda_servico",
    joinColumns = @JoinColumn(name = "venda_id"),
    inverseJoinColumns = @JoinColumn(name = "servico_id")
  )
  private List<Servico> servicos;

  @ManyToOne
  @JoinColumn(name = "veiculo")
  private Veiculo veiculo;

  //private List<Anotacao> anotacoes;

}
