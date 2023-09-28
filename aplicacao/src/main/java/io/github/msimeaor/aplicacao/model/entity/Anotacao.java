package io.github.msimeaor.aplicacao.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "anotacao")
public class Anotacao {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;

  @Column(name = "anotacao", length = 255, nullable = false)
  private String anotacao;

  @Column
  private LocalDate data;

  @ManyToOne
  @JoinColumn(name = "venda")
  private Venda venda;

}
