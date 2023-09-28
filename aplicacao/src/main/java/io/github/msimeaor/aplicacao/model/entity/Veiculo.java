package io.github.msimeaor.aplicacao.model.entity;

import io.github.msimeaor.aplicacao.enums.Fabricantes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "veiculo")
public class Veiculo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;

  @Column(name = "nome", length = 100, unique = true, nullable = false)
  private String nome;

  @Column(name = "placa", length = 7, nullable = false, unique = true)
  private String placa;

  @Enumerated(EnumType.STRING)
  @Column(name = "fabricante", nullable = false)
  private Fabricantes fabricante;

  @Column(name = "km_atual", length = 7, nullable = false)
  private String kmAtual;

  @Column(name = "observacao", length = 255)
  private String observacao;

  @OneToMany(mappedBy = "veiculo")
  private List<Venda> vendas;

}
