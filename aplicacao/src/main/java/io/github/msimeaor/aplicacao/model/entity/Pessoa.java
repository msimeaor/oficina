package io.github.msimeaor.aplicacao.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "pessoa")
public class Pessoa {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;

  @Column(name = "email", length = 100, unique = true)
  private String email;

  @Column(nullable = false)
  private boolean inativo;

  @Column(name = "cpf", length = 14, unique = true)
  private String cpf;

  @Column(name = "sexo", length = 9, nullable = false)
  private String sexo;

  @Column(name = "nome", length = 50, nullable = false)
  private String nome;

  @Column(name = "data_nascimento")
  private LocalDate dataNascimento;

  @OneToMany(mappedBy = "pessoa")
  private List<Telefone> telefones;

  @ManyToOne
  @JoinColumn(name = "endereco")
  private Endereco endereco;

  //private List<Venda> vendas;

}
