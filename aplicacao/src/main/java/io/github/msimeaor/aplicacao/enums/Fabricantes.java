package io.github.msimeaor.aplicacao.enums;

public enum Fabricantes {

  FIAT("Fiat"),
  VOLKSWAGEN("Volkswagen"),
  CHEVROLET("Chevrolet"),
  FORD("Ford"),
  TOYOTA("Toyota"),
  HYUNDAI("Hyundai"),
  RENAULT("Renault"),
  HONDA("Honda"),
  JEEP("Jeep"),
  NISSAN("Nissan"),
  PEUGEOT("Peugeot"),
  CITROEN("Citroën"),
  MITSUBISHI("Mitsubishi"),
  MERCEDES("Mercedes"),
  BMW("BMW"),
  AUDI("Audi"),
  KIA("Kia"),
  VOLVO("Volvo"),
  SUBARU("Subaru"),
  CHERY("Chery");

  private final String nome;

  Fabricantes(String nome) {
    this.nome = nome;
  }

  public String getNome() {
    return nome;
  }

  public Fabricantes getByString(String marca) {
    for (Fabricantes m : Fabricantes.values()) {
      if (m.getNome().equalsIgnoreCase(marca)) {
        return m;
      }
    }

    throw new IllegalArgumentException("Fabricante não encontrada!");
  }

}
