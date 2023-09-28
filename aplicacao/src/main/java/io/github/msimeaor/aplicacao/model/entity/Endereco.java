package io.github.msimeaor.aplicacao.model.entity;

import io.github.msimeaor.aplicacao.enums.UFs;
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
@Table(name = "endereco")
public class Endereco {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;

  @Column(name = "logradouro", length = 100)
  private String logradouro;

  @Enumerated(EnumType.STRING)
  @Column
  private UFs uf;

  @OneToMany(mappedBy = "endereco")
  private List<Pessoa> pessoas;

}
