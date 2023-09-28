package io.github.msimeaor.aplicacao.enums;

public enum FormasPagamento {

  DINHEIRO("Dinheiro"),
  DEBITO("Débito"),
  CREDITO("Crédito"),
  BOLETO("Boleto Bancário"),
  TRANSFERENCIA("Transferência Bancária"),
  PIX("PIX"),
  CHEQUE("Cheque");

  private final String descricao;

  FormasPagamento(String descricao) {
    this.descricao = descricao;
  }

  public String getDescricao() {
    return descricao;
  }

  public FormasPagamento getByString(String formaPagamento) {
    for (FormasPagamento forma : FormasPagamento.values()) {
      if (forma.getDescricao().equalsIgnoreCase(formaPagamento)) {
        return forma;
      }
    }

    throw new IllegalArgumentException("Forma de pagamento inválida!");
  }

}
