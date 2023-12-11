package io.github.msimeaor.aplicacao.model.utilities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.Link;

import java.lang.reflect.Method;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HateoasLinkBuilder {

  private Class<?> controllerClass;
  private String nomeMetodo;
  private Class<?>[] tiposParametros;
  private String filtroBusca;

  public Link gerarLink(Class<?> controllerClass, String nomeMetodo) {
    setarAtributos(controllerClass, nomeMetodo,
            new Class[]{Integer.class, Integer.class, String.class}, null);

    Method metodo = recuperarMetodo();
    return linkTo(metodo).withSelfRel();
  }

  private void setarAtributos(Class<?> controllerClass, String nomeMetodo,
                              Class<?>[] tiposParametros, String filtroBusca) {

    this.controllerClass = controllerClass;
    this.nomeMetodo = nomeMetodo;
    this.tiposParametros = tiposParametros;
    this.filtroBusca = filtroBusca;
  }

  private Method recuperarMetodo() {
    Method metodo = null;

    try {
      metodo = this.controllerClass.getMethod(this.nomeMetodo, this.tiposParametros);
    } catch (NoSuchMethodException ex) {
      ex.printStackTrace();
    }

    return metodo;
  }

  public Link gerarLinkFiltrando(Class<?> controllerClass, String nomeMetodo, String filtroBusca) {
    setarAtributos(controllerClass, nomeMetodo,
            new Class[]{String.class, Integer.class, Integer.class, String.class}, filtroBusca);

    Method metodo = recuperarMetodo();
    return linkTo(metodo, new Object[]{filtroBusca, null, null, null}).withSelfRel();
  }


}
