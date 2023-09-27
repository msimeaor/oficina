package io.github.msimeaor.aplicacao.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "telefone")
public class Telefone {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;

  @Column(name = "numero", length = 11, unique = true)
  private String numero;

  @ManyToOne
  @JoinColumn(name = "pessoa")
  private Pessoa pessoa;

}
